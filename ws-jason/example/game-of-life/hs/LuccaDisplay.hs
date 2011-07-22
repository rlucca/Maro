{-# OPTIONS -fglasgow-exts #-}

-- trocar IORef por STRef
module LuccaDisplay (
    GLWidget,
    renderize, createDisplay, updateDisplay
) where

import Qtc.Classes.Qccs
import Qtc.Classes.Qccs_h
import Qtc.Classes.Gui
import Qtc.ClassTypes.Opengl
import Qtc.ClassTypes.Gui
import Qtc.ClassTypes.Core
import Qtc.Core.Base
import Qtc.Gui.Base
import Qtc.Core.QSize
import Qtc.Gui.QApplication
import Qtc.Gui.QHBoxLayout
import Qtc.Gui.QWidget
import Qtc.Gui.QColor
import Qtc.Gui.QMouseEvent
import Qtc.Opengl.QGLWidget
import Qtc.Opengl.QGLWidget_h

import Data.IORef

import Graphics.Rendering.OpenGL as GL

import LuccaModel
import LuccaData


createDisplay :: SyncModel -> IO GLWidget
createDisplay model = do
    app <- qApplication ()
    w <- gLWidget
    pos <- newIORef (0,0,False)
    sz <- newIORef((0::GLdouble),(0::GLdouble))
    setHandler w "initializeGL()" $ initgl pos sz model
    setHandler w "(QSize)minimumSizeHint()" $ minSizeHint
    setHandler w "(QSize)sizeHint()" $ szHint
    setHandler w "mousePressEvent(QMouseEvent*)" $ msPressEvent pos
    return w

renderize :: GLWidget -> IO ()
renderize w = do
    root <- myQWidget
    layout <- qHBoxLayout ()
    addWidget layout w
    setLayout root layout
    qshow root ()
    ok <- qApplicationExec ()
    returnGC

msPressEvent :: IORef(Int, Int, Bool) -> GLWidget -> QMouseEvent () -> IO ()
msPressEvent pos this mev = do
    mx <- qx mev ()
    my <- qy mev ()
    modifyIORef pos $ \(px, py, _) -> (mx, my, True)
    updateGL this ()

minSizeHint :: GLWidget -> IO (QSize ())
minSizeHint _ = qSize (100::Int, 100::Int)

szHint :: GLWidget -> IO (QSize ())
szHint _ = qSize (600::Int, 600::Int)

initgl :: IORef(Int, Int, Bool) -> IORef(GLdouble, GLdouble) -> SyncModel -> GLWidget -> IO ()
initgl pos sz model this =
    do
    tp <- qColorFromRgbF (0.0::Double, 0.0::Double, 0.0::Double, 0.0::Double)
    qglClearColor this tp
    shadeModel $= Flat
    depthFunc $= Just Less
    cullFace $= Just Back
    setHandler this "resizeGL(int,int)" $ rsz sz
    setHandler this "paintGL()" $ dsply pos sz model
    return ()

rsz :: IORef(GLdouble, GLdouble) -> GLWidget -> Int -> Int -> IO ()
rsz sz this x y
  = do
    let
        side = min x y
        mx = (fromIntegral x / 60) :: GLdouble
        my = (fromIntegral y / 60) :: GLdouble
    GL.viewport $= (Position 0 0, GL.Size (fromIntegral x) (fromIntegral y))
    matrixMode $= Projection
    loadIdentity
    ortho 0 (fromIntegral x) (fromIntegral y) 0 1.0 10.0
    matrixMode $= Modelview 0
    modifyIORef sz $ \(sx, sy) -> (mx, my)
    return ()

updateModel :: Model -> IORef (Int, Int, Bool) -> IORef(GLdouble, GLdouble) -> IO ()
updateModel model pos size = do
    (rx, ry, flag) <- readIORef pos
    (sx, sy) <- readIORef size
    modifyIORef pos $ \(rx,ry,_) -> (rx,ry,False)
    if flag == True then
        let
            x = (fromIntegral rx) / sx
            y = (fromIntegral ry) / sy
            v = (truncate x) + (truncate y) * 60
        in viewerRealizeByPos model (v + 1)
     else
        return ()

--      1 2 3  lc                       l-1
--    1 a b c  11 a     12 b    13 c    0
--    2 d e f  21 d     22 e    23 f    1
--    3 g h i  31 g     32 h    33 i    2
--  1 2 3 4 5 6 7 8 9
--  a b c d e f g h i       -> 3*(l-1) + c

drawElem :: Int -> [Bool] -> GLdouble -> GLdouble -> IO ()
drawElem _ [] _ _ = return ()
drawElem current (x:xs) sx sy  = do
    let
        line = div current 60
        column = mod current 60
        x1 = sx * (fromIntegral column) :: GLdouble
        x2 = x1 + sx
        y1 = sy * (fromIntegral line) :: GLdouble
        y2 = y1 + sy
    drawElem (current + 1) xs sx sy
    if x == True then
        renderPrimitive TriangleStrip $ do
            GL.color $ Color3 (1.0::GLfloat) (1.0::GLfloat) (1.0::GLfloat)
            vertex $ Vertex3 x1 y1 (-5.0)
            vertex $ Vertex3 x2 y1 (-5.0)
            vertex $ Vertex3 x2 y2 (-5.0)
            vertex $ Vertex3 x1 y2 (-5.0)
            vertex $ Vertex3 x1 y1 (-5.0)
            vertex $ Vertex3 x2 y1 (-5.0)
            return ()
     else
        return ()

drawModel :: Model -> IORef(GLdouble, GLdouble) -> IO ()
drawModel model size = do
    (sx,sy) <- readIORef size
    elements <- getTable model
    drawElem 0 elements sx sy
    returnGC

dsply :: IORef (Int, Int, Bool) -> IORef(GLdouble, GLdouble) -> SyncModel -> GLWidget -> IO ()
dsply pos sz model this = do
    model' <- lock model
    GL.clear [ ColorBuffer, DepthBuffer ]
    loadIdentity
    updateModel model' pos sz -- liberar o nao o usuario ficar clicando
    drawModel model' sz
    unlock model model'
    returnGC


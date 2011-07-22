-- {-# LANGUAGE TemplateHaskell #-}
-- comentario acima por causa da anotacao pra gerar a instancia automatica
-- e ainda deve se usar -hide-package haxr-th-3000.5 no ghc e -hide-package monads-fd
module LuccaData where

import Control.Concurrent.MVar
import Data.Array
import Data.IORef
-- geracao automatica de instancias
--import Network.XmlRpc.THDeriveXmlRpcType
import Network.XmlRpc.Internals

import Qtc.ClassTypes.Opengl
import Qtc.ClassTypes.Gui
import Qtc.Core.Base
import Qtc.Gui.QWidget
import Qtc.Opengl.QGLWidget

-- Utilizado no Model
data Model = Model {
        tableSize :: Int, -- n x n
        table :: IORef (Array Int Bool), -- Integer key Bool value
        acted :: IORef [Int],
        step :: IORef Int
    }
type SyncModel = MVar Model

-- Utilizado no Display
type MyQWidget = QWidgetSc (CMyQWidget)
data CMyQWidget = CMyQWidget

type GLWidget = QGLWidgetSc (CGLWidget)
data CGLWidget = CGLWidget


-- Utilizado em WS
type Action = Percept
data Percept = Percept {
        functor::String,
        params::[String],
        annots::[Percept]
    }
    deriving (Show,Eq,Ord)
data TPerception = Perceptions {
        global::[Percept],
        local::[(String, Percept)],
        updated::[String]
    }
    deriving (Show)
-- Comentando geracao de templates automaticos
-- $(asXmlRpcStruct ''Percept)
-- $(asXmlRpcStruct ''TPerception)

instance XmlRpcType Percept where
    toValue p = toValue $ [toValue (functor p), toValue (params p), toValue (annots p)]
    fromValue v = do
        (ValueArray (fu:pa:an:[])) <- fromValue v
        fu' <- fromValue fu
        pa' <- fromValue pa
        an' <- fromValue an
        return $ Percept fu' pa' an'
    getType _ = TArray

-- Codigo
myQWidget :: IO (MyQWidget)
myQWidget = qSubClass (qWidget ())

gLWidget :: IO (GLWidget)
gLWidget = qSubClass (qGLWidget ())

updateDisplay :: GLWidget -> IO ()
updateDisplay w = do
    updateGL w ()


module LuccaModel (
    Model, SyncModel,
    newModel, cleanModel, realize, viewerRealizeByPos, getTable, lock, unlock, getStep,
    getNeighbors
) where

import Control.Concurrent.MVar
import Data.List.Ordered
import Control.Monad (when, fail)
import System.Random
import Data.IORef
import Data.Array

import LuccaData


newModel :: Int -> IO Model
newModel n = do
    let
        start = 1
        end = n*n
        valueDefault = False
    l <- randomizeData [start..end] 15
    t <- newIORef $ array(start,end) l
    u <- newIORef []
    r <- newIORef 0
    return $ Model end t u r

rollDice :: Int -> Int -> IO Int
rollDice s e = getStdRandom (randomR (s,e))

randomizeData :: [Int] -> Int -> IO [(Int, Bool)]
randomizeData [] percentageTrue = return []

randomizeData (key:keys) percentageTrue = do
    limited <- rollDice 1 100
    let value = if (limited < percentageTrue) then True else False
    ret <- randomizeData keys percentageTrue
    return $ (key,value):ret

lock :: SyncModel -> IO Model
lock = takeMVar

unlock :: SyncModel -> Model -> IO ()
unlock = putMVar

-- Model deve ser o valor retirado do SyncModel
getTable :: Model -> IO [Bool]
getTable model = do
    table' <- readIORef $ table model
    return $ elems table'

getTableSize :: Model -> (Int, Int)
getTableSize model = (1, tableSize model)

------------------------------------------
-- This is only executed in server thread
------------------------------------------

realize :: SyncModel -> String -> Maybe Bool -> IO Bool
realize controller name flag = do
    model <- lock controller
    let n = (read (drop 4 name)) :: Int
    updateCellList model n
    realize2 model n flag
    bool <- compareLengthCellList model
    unlock controller model
    return bool

realize2 :: Model -> Int -> Maybe Bool -> IO ()
realize2 model cell Nothing = return () -- ignore. Do nothing

realize2 model cell (Just flag) = do -- the cell can be activate or deactive
    t <- readIORef $ table model
    let t' = t // [ (cell, flag) ]
    modifyIORef (table model) $ \_ -> t'
    return ()

updateCellList :: Model -> Int -> IO ()
updateCellList model cell = do
    u <- readIORef $ acted model
    let u' = insertSet cell u
    modifyIORef (acted model) $ \_ -> u'

compareLengthCellList :: Model -> IO Bool
compareLengthCellList model = do
    t <- readIORef $ acted model
    return $ (tableSize model) == (length t)

cleanModel :: Model -> IO ()
cleanModel model = do
    modifyIORef (acted model) $ \_ -> [] -- cleaning notified agents!
    s <- readIORef $ step model
    modifyIORef (step model) $ \s -> (s + 1)
    return ()

getStep :: Model -> IO Int
getStep model = readIORef $ step model

----------------------------------------------
-- This point is executed in Display Thread
----------------------------------------------

viewerRealizeByPos :: Model -> Int -> IO ()
viewerRealizeByPos model pos = do
    flag <- realize2 model pos (Just True)
    return ()

getNeighbors :: Model -> IO [(String,Int)]
getNeighbors model = do
    elems <- getTable model
    let
        limits@(start,end) = getTableSize model
        line = truncate $ sqrt $ fromIntegral end
    return $ getNeighbors2 limits line elems start

getNeighbors2 :: (Int,Int) -> Int -> [Bool] -> Int -> [(String,Int)]
getNeighbors2 limits@(_,end) nline elems cell =
    let
        cnl = check_neighbors_live limits nline elems cell
        next = getNeighbors2 limits nline elems (cell + 1)
        name = "cell" ++ show cell
    in if cell == end then
        [(name, cnl)]
    else
        [(name, cnl)] ++ next

check_neighbors_live :: (Int,Int) -> Int -> [Bool] -> Int -> Int
check_neighbors_live limits@(_,end) nline elems cell =
    alive_neighbors
    where
        alive_neighbors = check idx [] elems
        idx = limitedIdx limits mapIdx
        mapIdx = [linePS..(linePS+2)] ++ [lineAS..(lineAS+2)] ++ [(cell-1),(cell+1)]
        linePS = cell - 1 - nline
        lineAS = cell - 1 - nline

check :: [Int] -> [Bool] -> [Bool] -> Int
check [] bs _ = countTrue bs
check (idx:idxs) bs elems =
    let
        el = take 1 $ drop (idx-1) elems
        bs' = el ++ bs
    in check idxs bs' elems

limitedIdx :: (Int, Int) -> [Int] -> [Int]
limitedIdx _ [] = []
limitedIdx l@(s,e) (i:is) =
    if i >= 1 && i <= e then
        [i] ++ limitedIdx l is
    else
        limitedIdx l is

countTrue :: [Bool] -> Int
countTrue [] = 0
countTrue (True:bs) = 1 + countTrue bs
countTrue (False:bs) = 0 + countTrue bs

{-
 s x d      o = 5, e = 9
 x o x      s = o - (sqrt(e) - 1)
 w x e      d = s + 2, w = o + (sqrt(e) - 1), e = w + 2
-}

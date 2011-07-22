module Main
where
import Control.Concurrent ( forkIO )
import Control.Concurrent.MVar
import LuccaDisplay
import LuccaModel
import LuccaWS
-- no ghc passar
--    -threaded
--    -hide-package haxr-th-3000.5
--    -hide-package monads-fd
main = do
    conf <- setup 8079
    model <- newModel 60
    control <- newEmptyMVar
    display <- createDisplay control
    forkIO $ serving conf control display
    putMVar control model -- send the notify to serving start
    model' <- takeMVar control -- when this finish the serving start end
    putMVar control model' -- and we need put the new model again
    renderize display

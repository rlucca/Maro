module Main
where

import Control.Concurrent ( forkIO )
import Control.Concurrent.MVar

import LuccaDisplay
import LuccaModel
import LuccaWS

-- nao esquecer de passar -threaded no ghc!!
main = do
    conf <- setup 8079
    model <- newModel 60
    control <- newEmptyMVar

    display <- createDisplay control
    forkIO $ serving conf control display
    putMVar control model -- send the notify to serving start
    model' <- takeMVar control -- when this finish the serving start end
    putMVar control model' -- and we need put the new model again in channel and start rendering thread
    renderize display

{-
    TODO
        Percepcoes nos agentes
        vamos contar os steps?
        como fazer para o usuario clicar e o WS atualizar o modelo concorrentemente?
-}

module LuccaWS (
    serving,
    setup
) where
import Network.Socket
import Network.XmlRpc.Server
import Network.XmlRpc.Internals
import Data.IORef
import Data.List.Ordered
import Happstack.Server.SimpleHTTP
import Control.Monad.Trans
import Data.ByteString.Lazy.Char8 (unpack)
import LuccaModel
import LuccaData
handshake :: String -> IO Int
handshake s =
    if key == 'i' then
        return result
    else
        return $ -1
    where
        key = head s
        value = read (tail s) :: Int
        m = mod value 2
        result =
            if m == 0 then
                truncate $ fromIntegral(value) / 2
            else
                1 + value * 3
addPercept :: IORef(TPerception) -> Percept -> IO Bool
addPercept p perception = do
    (Perceptions gp lp ua) <- readIORef p
    let gp' = insertSet perception gp
    modifyIORef p $ \(Perceptions _ lp _) -> (Perceptions gp' lp [])
    return True
addPerceptLocal :: IORef(TPerception) -> String -> Percept -> IO Bool
addPerceptLocal p name perception = do
    (Perceptions gp lp ua) <- readIORef p
    let lp' = insertSet (name,perception) lp
    modifyIORef p $ \(Perceptions gp _ _) -> (Perceptions gp lp' [])
    return True
havePercepts :: IORef(TPerception) -> String -> IO Bool
havePercepts p name = do
    pp <- readIORef p
    return $ not $ (updated pp) `has` name
getPercepts :: IORef(TPerception) -> String -> Bool -> IO [Percept]
getPercepts p name isUpdate = do
    (Perceptions gp lp ua) <- readIORef p
    -- r eh a uniao das percepcoes de gp com as percepcoes (segundo
    -- elemento) dos dados locais filtrados pelo nome
    let r = union gp [lp' | (n, lp') <- lp, n == name]
    if isUpdate == False then
        return r
     else do
        let ua' = insertSet name ua
        modifyIORef p $ \(Perceptions gp lp _) -> (Perceptions gp lp ua')
        return r
removePercept :: IORef(TPerception) -> Percept -> IO Bool
removePercept p perception = do
    (Perceptions gp lp ua) <- readIORef p
    let gp' = gp `minus` [perception]
    modifyIORef p $ \(Perceptions _ lp _) -> (Perceptions gp' lp [])
    return True
removePerceptLocal :: IORef(TPerception) -> String -> Percept -> IO Bool
removePerceptLocal p name perception = do
    (Perceptions gp lp ua) <- readIORef p
    let lp' = lp `minus` [(name,perception)]
    modifyIORef p $ \(Perceptions gp _ _) -> (Perceptions gp lp' [])
    return True
clearPercepts :: IORef(TPerception) -> IO Bool
clearPercepts p = do
    (Perceptions gp lp ua) <- readIORef p
    if (null gp) then
        return True
     else do
        modifyIORef p $ \(Perceptions _ lp _) -> (Perceptions [] lp [])
        return True
clearPerceptsLocal :: IORef(TPerception) -> String -> IO Bool
clearPerceptsLocal p name = do
    (Perceptions gp lp ua) <- readIORef p
    if (null gp) then
        return True
     else do
        -- lp' eh o lp menos as tuplas que tem como primeiro elemento name
        let lp' = lp `minus` [(name, perception) | (n, perception) <- lp, n==name]
        modifyIORef p $ \(Perceptions _ lp _) -> (Perceptions [] lp [])
        return True
createAllPerceptions :: IORef(TPerception) -> [(String,Int)] -> Int -> IO ()
createAllPerceptions _ [] _ = return ()
createAllPerceptions p ((c@(n,a)):cs) step = do
    addPerceptLocal p n (Percept "alive_neighbors" [show a] [])
    addPerceptLocal p n (Percept "step" [show step] [])
    createAllPerceptions p cs step
turnPercepts :: IORef(TPerception) -> Model -> IO ()
turnPercepts p m = do
    modifyIORef p $ \(Perceptions _ _ _) -> (Perceptions [] [] [])
    step <- getStep m
    neighborsList <- getNeighbors m
    createAllPerceptions p neighborsList step
cmdRealize :: IORef(TPerception) -> SyncModel -> GLWidget -> String -> Maybe Bool -> IO Bool
cmdRealize p c v n f = do
    nextStep <- realize c n f
    if nextStep then
        do
            model <- lock c
            cleanModel model
            turnPercepts p model
            unlock c model
            updateDisplay v
            return True
     else
        return True
performAction :: IORef(TPerception) -> SyncModel -> GLWidget -> String -> Action -> IO Bool
performAction p controller viewer name action =
    let
        actionName = functor action
        actionPars = params action
        actionBy = name
    in
        case actionName of
            "skip"  -> cmdRealize p controller viewer actionBy Nothing
            "live"  -> cmdRealize p controller viewer actionBy (Just True)
            "die"   -> cmdRealize p controller viewer actionBy (Just False)
            _       -> return True -- ignore action
createAllPerceptionsBlock :: IORef(TPerception) -> SyncModel -> IO ()
createAllPerceptionsBlock p c = do
    model <- lock c
    step <- getStep model
    n <- getNeighbors model
    createAllPerceptions p n step
    unlock c model
serving :: (Socket, Conf) -> SyncModel -> GLWidget -> IO ()
serving d@(socket, conf) controller viewer = do
    internalData <- newIORef(Perceptions [] [] [])
    createAllPerceptionsBlock internalData controller
    let
        conf = Conf 8079 Nothing
        ms = [
                ("EM.handshake", fun          $ handshake),
                ("EM.addPercept", fun         $ addPercept internalData),
                ("EM.addPerceptLocal", fun    $ addPerceptLocal internalData),
                ("EM.havePercepts", fun       $ havePercepts internalData),
                ("EM.getPercepts", fun        $ getPercepts internalData),
                ("EM.removePercept", fun      $ removePercept internalData),
                ("EM.removePerceptLocal", fun $ removePerceptLocal internalData),
                ("EM.clearPercepts", fun      $ clearPercepts internalData),
                ("EM.clearPerceptsLocal", fun $ clearPerceptsLocal internalData),
                ("EM.performAction", fun      $ performAction internalData controller viewer)
            ]
        handler = do
            Body b <- fmap rqBody askRq
            liftIO $ handleCall (methods ms) (unpack b)
    simpleHTTPWithSocket socket conf handler
setup :: Int -> IO (Socket, Conf)
setup port = do
    let c = Conf port Nothing
    s <- bindPort c
    return (s, c)

from SimpleXMLRPCServer import SimpleXMLRPCServer
from random import randint

#allow_none should be die!
server = SimpleXMLRPCServer(("localhost", 8079))

server.register_introspection_functions()

updateA = []
perceptLG = []
perceptLL = {}

def eraseList(L): # ok ok, isso conclui-se que nao sei python... :o
	try:
		while True:
			L.pop(0) #arg
	except IndexError:
		pass
	return None

def handshake(challenge):
	"""handshake :: String -> Integer

	This function receive a Challenge and send the Answer to sender.
	"""
	if challenge[0] != 'i':
		return -1
	value = int(challenge[1:])
	if (value % 2) == 0:
		return value / 2
	return 1 + value * 3

def addPercept(literal):
	"""addPercept :: (functor, params, metadata) -> Bool
	This function add the perception received in global perceptions."""
	try:
		i = perceptLG.index( literal )
	except ValueError:
		try:
			perceptLG.append( literal )
			eraseList(updateA)
		except:
			return False
	return True

def addPerceptLocal(agName, literal):
	"""addPercept :: String -> (functor, params, metadata) -> Bool
	This function add a perception to key specifid by String."""
	try:
		agPercept = perceptLL[agName]
		if literal in agPercept:
			return True
		agPercept.append( literal )
		perceptLL[agName] = agPercept
	except KeyError:
		perceptLL[agName] = [ literal ]
	except:
		return False
	eraseList(updateA)
	return True

def havePercepts(agName):
	if agName in updateA:
		return False
	return True

def getPercepts(agName, onlyUpdate):
	"""getPercepts :: String -> Bool -> [(functor, params, metadata)]
	This function receive a agent name and return the perceptions of the agent.
	The flag is used to upgrade the vector of sended perceptions."""
	glPercept = list(perceptLG) # be careful dont use attribution direct!
	agPercept = []
	try:
		agPercept = perceptLL[agName]
	except:
		pass
	if onlyUpdate == True:
		updateA.append(agName)
	# append in a copy of global perceptions all agent perceptions not existed yet
	# list(set(a+b)) cannot be used because list is unhashable :'(
	for p in agPercept:
		if (p in glPercept) == False:
			glPercept.append(p)
	return glPercept

def removePerceptLocal(agName, literal):
	"""removePerceptLocal :: String -> (functor, params, metadata) -> Bool
	remove the percept informed in agent specified."""
	try:
		agPercept = perceptLL[agName]
		try:
			agPercept.remove(literal)
			eraseList(updateA)
			return True
		except ValueError: # remove not locate literal
			return True
	except:
		pass
	return False

def removePercept(literal):
	"""removePercept :: (functor, params, metadata) -> Bool
	remove the percept informed at global perception."""
	try:
		perceptLG.remove(literal)
		eraseList(updateA)
		return True
	except ValueError:
		return True
	except:
		pass
	return False
	
def clearPerceptsLocal(agName):
	"""clearPerceptsLocal :: String -> Bool
	Erase all local perceptions of a agent"""
	try:
		agPercept = perceptLL[agName]
		if len(agPercept) > 0:
			eraseList(agPercept)
			eraseList(updateA)
		return True
	except KeyError:
		return True
	except:
		pass
	return False

def clearPercepts():
	"""clearPercept :: Bool
	Erase all global perceptions"""
	try:
		if len(perceptLG) > 0:
			eraseList(perceptLG)
			eraseList(updateA)
		return True
	except:
		pass
	return False

def performAction(agName, s):
	"""performAction :: String -> (functor, params, nulled_data) -> Bool
	The agent agName will execute the action especified in functor with params."""
	action = s[0]
	parameters = s[1]
	metadata = s[2]
	if action == "hello":
		if addPercept(["world", [], []]) == False:
			return False # error
	elif action == "clean":
		if clearPercepts() == False:
			return False
	#print "agent %s executing %s" % (agName, action)
	return True

server.register_function(handshake, "EM.handshake")
server.register_function(havePercepts, "EM.havePercepts")
server.register_function(getPercepts, "EM.getPercepts")
server.register_function(addPercept, "EM.addPercept")
server.register_function(addPerceptLocal, "EM.addPerceptLocal")
server.register_function(removePercept, "EM.removePercept")
server.register_function(removePerceptLocal, "EM.removePerceptLocal")
server.register_function(clearPercepts, "EM.clearPercepts")
server.register_function(clearPerceptsLocal, "EM.clearPerceptsLocal")
server.register_function(performAction, "EM.performAction")

server.serve_forever();

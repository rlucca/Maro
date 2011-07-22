from SimpleXMLRPCServer import SimpleXMLRPCServer
from random import randint

server = SimpleXMLRPCServer(("localhost", 8080))

server.register_introspection_functions()

agentL = {}

def handshake(challenge):
	if challenge[0] != 'i':
		return int(-1)
	ret = int(challenge[1:])
	ret *= ret;
	return ret

def login(agent_name):
	max = 2 ** 20
	pseudo = randint(1, max)
	try:
		temp = agentL[pseudo]
		while True:
			pseudo = (pseudo + 1) % max
			temp = agentL[pseudo]
	except KeyError:
		# TODO ao inves de agent_name, uma classe agente?
		agentL[pseudo] = (agent_name, [])
		#print "Create ", agent_name, "referenced by", pseudo
	return pseudo

def logout(id):
	try:
		ag = agentL.pop(id)
		# TODO chamar o destrutor do ag?
		#print "Logout of ", id, "OK!"
		return True
	except:
		#print "Logout of ", id, "failed!"
		return False

def perceive(id, percepts): #TODO
	# save percepts and call act to get return the next action
	#print "listL", percepts
	data = agentL[id]
	agentL[id] = (data[0], percepts)
	return act(id, False)

def act(id, eraseAll): #TODO
	# eraseAll True erase the percepts saved previous
	# this function return the next action to the agent
	if eraseAll:
		data = agentL[id]
		agentL[id] = (data[0], [])
	data = agentL[id]
	actions = { 0: "die", 2: "skip", 3:"live" }
	return actions[aliveNeighbors(data[1])]

def aliveNeighbors(perceptArray):
	valid = [ 2, 3 ]
	for elem in perceptArray:
		if "alive_neighbors" == elem[0]:
			for v in valid:
				if v == int(elem[1][0]):
					return v
	return 0

def receiveMessages(id, messages): # TODO
	print "self", messages
	return sendMessages(id)

def sendMessages(id): # TODO [ "<msgid,,typeOfMessage,Receiver,Message>", ... ]
	return []

server.register_function(handshake, "PM.handshake")
server.register_function(login, "PM.login")
server.register_function(logout, "PM.logout")
server.register_function(perceive, "PM.perceive")
server.register_function(act, "PM.act")
server.register_function(receiveMessages, "PM.checkMail")
server.register_function(sendMessages, "PM.getMessages")

server.serve_forever();

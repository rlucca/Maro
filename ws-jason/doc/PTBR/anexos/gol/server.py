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
		agentL[pseudo] = (agent_name, [])
	return pseudo
def logout(id):
	try:
		ag = agentL.pop(id)
		return True
	except:
		return False
def perceive(id, percepts):
	data = agentL[id]
	agentL[id] = (data[0], percepts)
	return act(id, False)
def act(id, eraseAll):
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
def receiveMessages(id, messages):
	print "self", messages
	return sendMessages(id)
def sendMessages(id):
	return []
server.register_function(handshake, "PM.handshake")
server.register_function(login, "PM.login")
server.register_function(logout, "PM.logout")
server.register_function(perceive, "PM.perceive")
server.register_function(act, "PM.act")
server.register_function(receiveMessages, "PM.checkMail")
server.register_function(sendMessages, "PM.getMessages")
server.serve_forever();

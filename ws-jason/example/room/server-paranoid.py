from SimpleXMLRPCServer import SimpleXMLRPCServer
from random import randint

server = SimpleXMLRPCServer(("localhost", 8081), logRequests=False)

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
	except KeyError: # TODO ao inves de agent_name, uma classe agente?
		agentL[pseudo] = (agent_name, [], [])
		print "Create ", agent_name, "referenced by", pseudo
	return pseudo

def logout(id):
	try:
		ag = agentL.pop(id) # TODO chamar o destrutor do ag?
		print "Logout of ", id, "OK!"
		return True
	except:
		print "Logout of ", id, "failed!"
		return False

def perceive(id, percepts): #TODO
	# save percepts and call act to get return the next action
	print "listL", percepts
	data = agentL[id]
	agentL[id] = (data[0], percepts, data[2])
	return act2(id, False)

def act2(id, eraseAll): #TODO
	# eraseAll True erase the percepts saved previous
	# this function return the next action to the agent
	if eraseAll:
		data = agentL[id]
		agentL[id] = (data[0], [], data[2])
		print "listL empty"
	data = agentL[id]
	if len(data[1]) > 0:
		sendMessage = exclusion_act(data[1])
		if len(sendMessage) > 0:
			data[2].append(sendMessage)
			agentL[id] = (data[0], data[1], data[2])
	return ""

def act(id, eraseAll):
	if eraseAll:
		return act2(id, eraseAll)
	return ""

def exclusion_act(perceptions):
	p = perceptions[0]
	functor = p[0]
	terms = p[1]
	annots = p[2]
	if functor == "~locked" and terms[0] == "door":
		try:
			msgId = agentL["msgId"]
		except KeyError:
			msgId = 0
		agentL["msgId"] = msgId + 1
		return "<mprpc%02d,,achieve,porter,locked(door)>" % msgId # por algum motivo ele nao preencheu :/ TODO
	return ""

def receiveMessages(id, messages): # TODO
	print "self", messages
	return sendMessages(id)

def sendMessages(id): # TODO [ "<msgid,,typeOfMessage,Receiver,Message>", ... ]
	data = agentL[id]
	ret = []
	if len(data[2]) > 0:
		ret = data[2]
		agentL[id] = (data[0], data[1], [])
	print "sending", ret
	return ret

server.register_function(handshake, "PM.handshake")
server.register_function(login, "PM.login")
server.register_function(logout, "PM.logout")
server.register_function(perceive, "PM.perceive")
server.register_function(act, "PM.act")
server.register_function(receiveMessages, "PM.checkMail")
server.register_function(sendMessages, "PM.getMessages")

server.serve_forever();

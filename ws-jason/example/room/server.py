from SimpleXMLRPCServer import SimpleXMLRPCServer
from random import randint

server = SimpleXMLRPCServer(("localhost", 8080), logRequests=False)

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
	return act(id, False)

def act(id, eraseAll): #TODO
	# eraseAll True erase the percepts saved previous
	# this function return the next action to the agent
	if eraseAll:
		data = agentL[id]
		agentL[id] = (data[0], [], data[2])
		print "listL empty"
	data = agentL[id]
	if len(data[2]) >= 1:
		ret = exclusion_act(data[1], data[2][0])
		if len(ret) > 0:
			agentL[id] = (data[0], data[1], data[2][1:])
		return ret
	return ""

def exclusion_act(perceptions, message_received):
	M = { "~locked(door)":"locked(door)", "locked(door)":"~locked(door)" }
	P = { "~locked":"lock", "locked":"unlock" }
	test = M[ message_received[4] ][:-1].split('(')
	for p in perceptions:
		functor = p[0]
		terms = p[1]
		annots = p[2]
		if functor == test[0] and terms[0] == test[1]:
			return P[functor]
	return ""

def receiveMessages(id, messages): # TODO
	print "self", messages
	data = agentL[id]
	msgL = []
	for msg in messages:
		m = msg[1:-1].split(',')
		if m[1] == "claustrophobe" and m[4] == "~locked(door)":
			msgL.append(m);
		elif m[1] == "paranoid" and m[4] == "locked(door)":
			msgL.append(m);
	agentL[id] = (data[0], data[1], msgL)
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

def messages_bytes(start_name, n_processes, starting_port, to_print=False):
    messagesTransmitted = 0
    messagesReceived = 0
    bytesTransmitted = 0
    bytesReceived = 0

    for port in range(starting_port, starting_port + n_processes):
        f = open(start_name.format(port), "r")
        finalBytesTransmitted = 0
        finalBytesReceived = 0
        finalMessagesTransmitted = 0
        finalMessagesReceived = 0

        for i in f:
            line = i.split(" ")

            if(line[0].__contains__('BytesSent')):
                finalBytesTransmitted = int(line[2])

            elif(line[0].__contains__('MessagesSent')):
                finalMessagesTransmitted = int(line[2])

            elif(line[0].__contains__('BytesReceived')):
                finalBytesReceived = int(line[2])

            elif(line[0].__contains__('MessagesReceived')):
                finalMessagesReceived = int(line[2])

        messagesTransmitted += finalMessagesTransmitted
        messagesReceived += finalMessagesReceived
        bytesTransmitted += finalBytesTransmitted
        bytesReceived += finalBytesReceived

    percentMessagesLost = (((messagesTransmitted - messagesReceived) / messagesTransmitted) * 100)

    if(to_print):
        print('Total Transmitted and Received Content:')
        print('Messages Transmitted: ', messagesTransmitted)
        print('Messages Received: ', messagesReceived)
        print('Bytes Transmitted: ', bytesTransmitted)
        print('Bytes Received: ', bytesReceived)
        print("Percentage of messages lost: {:.2f}%".format(percentMessagesLost))
        print()

    return messagesTransmitted, messagesReceived, bytesTransmitted, bytesReceived

start_name = "./results/results-Alexandres-MBP.lan-{}.txt"
n_processes = 5
starting_port = 5000

#messages_bytes(start_name, n_processes, starting_port)
import sys
import json
import requests

if __name__ == "__main__":
    
	url="https://fcm.googleapis.com/fcm/send"
	server_key="AIzaSyB3QhKVX5vEVa4VM7Wb0WvNuIH469HNZJA"
	alpha=10
	headers = {'Content-type': 'application/json', 'Authorization': 'key='+server_key }
	topic = "/topics/research-intervention"
	reg_ids = ["d65g_WO0z18:APA91bHxuB07aSpJyhOoMyhm087ZRLlWsl6Gym5DCA1OBvWNAVLWCXuGKRa1vBr4OlhrmrAJPgZ8n5Roi1kEHCcsiBJ1pFxk6TQjVKhE_oEijqlGXBNlLnkcvCC08zdvKvP1BmjQkMV8"]
	
	bin_type=sys.argv[1]

	data = {
		"bin_type" : bin_type,
	}

	body={
		"data": data,
	    "registration_ids": reg_ids,
	    # "to": "/topics/"+sys.argv[1]
	}

	r = requests.post(url, data=json.dumps(body), headers=headers)

	print(r.status_code, r.reason)
	print(r.text + '...')
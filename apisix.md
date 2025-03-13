### APISIX

```
docker-compose --version
git clone -b release/apisix-3.6.0 https://github.com/apache/apisix-docker.git
cd /usr/local/apisix-docker/example
docker-compose -p docker-apisix up -d




###

PUT http://127.0.0.1:9180/apisix/admin/routes/1
X-API-KEY: edd1c9f034335f136f87ad84b625c8f1
Content-Type: application/json


{
  "name": "smiling_auth",
  "plugins": {
    "forward-auth": {
      "uri": "http://10.20.13.162:8082/security/auth",
      "request_headers": ["Authorization"],
      "upstream_headers": ["X-User-ID"],
      "client_headers": ["Location"]
    },
    "cors": {},
    "prometheus":{
      "prefer_name": true
    },
    "file-logger": {
      "path": "logs/file.log"
    }
  },
  "uris": [
    "/smiling/question/*",
    "/smiling/dbsource/*",
    "/smiling/education/*"
  ],
  "upstream": {
    "type": "roundrobin",
    "nodes": {
      "10.20.13.162:8081": 1
    }
  }
}

###
PUT http://127.0.0.1:9180/apisix/admin/routes/2
X-API-KEY: edd1c9f034335f136f87ad84b625c8f1
Content-Type: application/json


{
  "name": "glm_auth",
  "uri": "/glm/*",
  "plugins": {
    "forward-auth": {
      "uri": "http://10.20.13.162:8082/security/auth",
      "request_headers": ["Authorization"],
      "upstream_headers": ["X-User-ID"],
      "client_headers": ["Location"]
    },
    "cors": {},
    "prometheus":{
      "prefer_name": true
    },
    "file-logger": {
      "path": "logs/file.log"
    }
  },
  "upstream": {
    "type": "roundrobin",
    "nodes": {
      "10.40.241.6:17862": 1
    }
  }
}



###

curl -X PUT -H "Content-Type: application/json" -H "X-API-KEY: edd1c9f034335f136f87ad84b625c8f1" -d '
{
  "name": "smiling_auth",
  "plugins": {
    "forward-auth": {
      "uri": "http://10.20.13.237:8082/security/auth",
      "request_headers": ["Authorization"],
      "upstream_headers": ["X-User-ID"],
      "client_headers": ["Location"],
      "timeout": 30000,
      "keepalive": false,
      "ssl_verify": false
    },
    "cors": {},
    "prometheus":{
      "prefer_name": true
    },
    "file-logger": {
      "path": "logs/file.log"
    }
  },
  "uris": [
    "/smiling/question/*",
    "/smiling/dbsource/*",
    "/smiling/education/*"
  ],
  "upstream": {
    "type": "roundrobin",
    "nodes": {
      "10.20.13.237:8081": 1
    }
  }
}' http://127.0.0.1:9180/apisix/admin/routes/1

###

curl -X PUT -H "Content-Type: application/json" -H "X-API-KEY: edd1c9f034335f136f87ad84b625c8f1" -d '
{
  "name": "glm_auth",
  "uri": "/glm/*",
  "plugins": {
    "forward-auth": {
      "uri": "http://10.20.13.237:8082/security/auth",
      "request_headers": ["Authorization"],
      "upstream_headers": ["X-User-ID"],
      "client_headers": ["Location"],
      "timeout": 30000,
      "keepalive": false,
      "ssl_verify": false
    },
    "cors": {},
    "prometheus":{
      "prefer_name": true
    },
    "file-logger": {
      "path": "logs/file.log"
    }
  },
  "upstream": {
    "type": "roundrobin",
    "nodes": {
      "10.40.241.6:17862": 1
    }
  }
}' http://127.0.0.1:9180/apisix/admin/routes/2 


###

curl -X PUT -H "Content-Type: application/json" -H "X-API-KEY: edd1c9f034335f136f87ad84b625c8f1" -d '
{
  "name": "glm_forward",
  "uri": "/screen/glm/*",
  "plugins": {
    "forward-auth": {
      "uri": "http://10.20.13.237:8082/security/forward/auth",
      "request_headers": ["Authorization"],
      "upstream_headers": ["X-User-ID"],
      "client_headers": ["Location"],
      "timeout": 30000,
      "keepalive": false,
      "ssl_verify": false
    },
    "cors": {},
    "prometheus":{
      "prefer_name": true
    },
    "file-logger": {
      "path": "logs/file.log"
    },
    "proxy-rewrite": {
                "regex_uri": ["^/screen/glm/(.*)", "/glm/$1"]
            }
  },
  "upstream": {
    "type": "roundrobin",
    "nodes": {
      "10.40.241.6:17862": 1
    }
  }
}' http://127.0.0.1:9180/apisix/admin/routes/3 

curl -X PUT -H "Content-Type: application/json" -H "X-API-KEY: edd1c9f034335f136f87ad84b625c8f1" -d '
{
  "name": "apps_hit_recommendation",
  "uri": "/apps_hit_recommendation",
  "plugins": {
    "cors": {},
    "prometheus":{
      "prefer_name": true
    },
    "file-logger": {
      "path": "logs/file.log"
    }
  },
  "upstream": {
    "type": "roundrobin",
    "nodes": {
      "10.40.241.6:17862": 1
    }
  }
}' http://127.0.0.1:9180/apisix/admin/routes/4 
```

###

curl -X PUT -H "Content-Type: application/json" -H "X-API-KEY: edd1c9f034335f136f87ad84b625c8f1" -d '
{
"name": "smiling_auth",
"plugins": {
"forward-auth": {
"uri": "http://10.20.13.237:8082/security/auth",
"request_headers": ["Authorization"],
"upstream_headers": ["X-User-ID"],
"client_headers": ["Location"],
"timeout": 30000,
"keepalive": false,
"ssl_verify": false
},
"cors": {},
"prometheus":{
"prefer_name": true
},
"file-logger": {
"path": "logs/file.log"
}
},
"uris": [
"/smiling/question/*",
"/smiling/dbsource/*",
"/smiling/edufile/*"
],
"upstream": {
"type": "roundrobin",
"nodes": {
"10.20.13.237:8081": 1
}
}
}' http://127.0.0.1:9180/apisix/admin/routes/1


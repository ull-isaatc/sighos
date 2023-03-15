### Protocol

#### Messages from server to client

##### Send of data

The server can send data to a client when requested by it. An example can be seen in the following:
```json
{
    "time": "2022-01-01T00:00:00.000Z", 
    "new": [
        {
            "id": 1,
            "type": "truck",
            "properties": {
                "width": 82,
                "height": 90,
                "x": 15.8,
                "y": 35.1
            }
        },
        {
            "id": 2,
            "type": "truck",
            "properties": {
                "width": 41,
                "height": 79,
                "x": 3.12,
                "y": 14.07              
            }
        }
    ],
    "update": [
        {
            "id": 7,
            "properties": {
                "x": 15.9
            }
        },
        {
            "id": 14,
            "properties": {
                "width": 7,
                "y": 81.13
            }
        }
    ],
    "delete": [ 13, 24, 71 ]
}
```
The fields are described as follows:
* `time`. Time associated with the data reported by the server.
* `new`. Data about new events. These events indicate the creation of entities in the system. This kind of events include the following fields:
  * `id`. Identifier of the new entity in the system.
  * `type`. Type of the new entity in the system.
  * `properties`. Map composed of the properties of the new entity. Their values indicate the initial values of these properties.
* `update`. Data about updat eevents. These events indicate the update of entities in the system. This kind of events include the following fields:
  * `id`. Identifier of the entity in the system to update.
  * `properties`. Map composed of the properties to update in the entity. Its values indicate the new values of these properties.
* `delete`. Data about deletion events. These events indicate the deletion of entities from the system. It is an array composed of the identifiers of the entities to delete from the system.

#### Messages from client to server

##### Request of time window

The client can request data to the server by indicating a time window. This can be done as follows:
```json
{ 
    "scenario": "santander",
    "min": 2.1,
    "max": 8.1,    
    "include_min": "true",
    "include_max": "true"
}
```
The fields are described as follows:
* `scenario`. Scenario whose data are requested.
* `min`. Minimum time required.
* `max`. Maximum time required.
* `include_min`. The minimum time `min` is included.
* `include_max`. The maximum time `min` is included.

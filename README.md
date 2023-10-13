# Consumable Tracker (Server)

This server uses Java with Spring Boot to implement a basic server keeping track of a JSON-based food item inventory. See [curlCommands.txt](docs/curlCommands.txt) for a full command list.

An MVC pattern is used for the file structure, and a factory pattern for creation of Consumable types. Gson is used for serializing and deserializing Consumable objects to/from JSON.

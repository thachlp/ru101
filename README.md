# ru101
## String
- [SET](https://redis.io/commands/set/): Set key to hold the value
  ```
   SET mykey "Hello World" nx; // Only set key if it does not exist
   SET mykey "Hello World" xx; // Only set key if it already exist  
  ```
- [GET](https://redis.io/commands/get/): Get the value of key, if the key is not exist, nill returned. An error return if the value stored at key is not a string.
- [INCR](https://redis.io/commands/incr/): Increase the number stored at key by one. An error is returned if the key contains a value of wrong type or string that can not be represented as integer.

# ru101
## Week 1
### Keys

- Binary safe: string, integer, float, object …, max size 512M
- Too long or too short are not a good idea, it should be meaningful and short
- Expiration can be set by millisecond, second, and UNIX time.
- Common used:
  - SET
  - GET
  - DEL/UNLINK
  - EXPIRED
  - PERSIST
  - EXISTS
  - KEYS/SCAN

### String

- Simple data structure.
- Storing text, numeric, serialized JSON, XML, and binary array.
- They were mostly used for caching combined with EXPIRE.
- Store and manipulate numeric, text values.

### Hash

- Values are pairs.
- Common used:
  - HSET
  - HGET if the field does not exist, `nil` is returned.
  - HDEL
  - HKEYS/HVALS
  - HEXISTS
- Can manipulate on-field values.
- Expiration on key, not by field

### Lists

- Can be used as a Stack or Queue.
- Common used:
  - LPUSH, RPUSH
  - LPOP, RPOP

### Sets

- Collections of unorder unique elements
- Common used:
  - SADD
  - SPOP
  - SMEMBERS
  - SREM
  - SDIFF diff elements from the first SET to follow SET

### Sorted Sets

- Order collections of unique value.
- Each element has an associate score, which can be increased or decreased.
- Can be accessed by accessing or descending order.
- Multi elements can have the same score.
- Common used:
  - ZADD
  - ZRANK range is Zero bases.
  - ZREVRANK//redis.io/commands/hget/): Return the value associate with field in the hash stored at key.
## Week 2
### Capped Collections & Set Operation

- Sorted Set:
  - Leaderboard
  - Using ****ZREMRANGEBYRANK****
- List
  - Stream activity
  - **LTRIM** or **RTRIM**

### Faceted Search

- Set and Sorted Set can be union, a score of Set value will be set to 1
## Week 3
### Transaction

- MULTI makes the start of a transaction block
- EXEC executes all commands issued after MULTI
- DISCARD discard all commands issued after MULTI, there are no commands to undo.
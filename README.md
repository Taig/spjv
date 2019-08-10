# Snowplow JSON validator

Lorem ipsum

## Assumptions & implementation remarks

- GET /schema/id returns a status message with an `getSchema` action
- Trying to upload the same schema id twice is an error (and does therefore not override)
- Release candidates / milestone dependencies are okay in the context of this project, I took this as an opportunity to look at new features such as the cats-effect `Blocker`
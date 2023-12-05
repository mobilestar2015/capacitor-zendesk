# capacitor-zendesk

Capacitor Zendesk Plugin

## Install

```bash
npm install capacitor-zendesk
npx cap sync
```

## API

<docgen-index>

* [`initialize(...)`](#initialize)
* [`setPrimaryColor(...)`](#setprimarycolor)
* [`startChat(...)`](#startchat)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initialize(...)

```typescript
initialize(config: ZendeskConfig) => Promise<void>
```

| Param        | Type                                                    |
| ------------ | ------------------------------------------------------- |
| **`config`** | <code><a href="#zendeskconfig">ZendeskConfig</a></code> |

--------------------


### setPrimaryColor(...)

```typescript
setPrimaryColor(primaryColor: ZendeskPrimaryColor) => Promise<void>
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`primaryColor`** | <code><a href="#zendeskprimarycolor">ZendeskPrimaryColor</a></code> |

--------------------



### startChat(...)

```typescript
startChat(config: ZendeskChat) => Promise<void>
```

| Param        | Type                                                |
| ------------ | --------------------------------------------------- |
| **`config`** | <code><a href="#zendeskchat">ZendeskChat</a></code> |

--------------------


### Interfaces


#### ZendeskConfig

| Prop           | Type                |
| -------------- | ------------------- |
| **`key`**      | <code>string</code> |


#### ZendeskPrimaryColor

| Prop               | Type                |
| ------------------ | ------------------- |
| **`primaryColor`** | <code>string</code> |


</docgen-api>

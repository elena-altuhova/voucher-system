
# Voucher System API

Voucher System is a simple REST API developed as a PoC to support redeeming, sending and generating vouchers for a marketing campaign.

## Run Locally

Clone the project

```bash
  git clone https://link-to-project
```

Go to the project directory

```bash
  cd my-project
```

Build a volume for a Database to persist your data

```bash
  docker volume create dbvouch
```

Build an image of app.
   ```
   docker build -t voucher-application:local -f application.dockerfile .
   ```

Run Docker compose, but specify correct app image if you changed one from above
```
  docker-compose up
```


## API Reference

#### Create new Marketing Campaign

```http
  POST /v1/campaigns
```

| Parameter   | Type         | Description                       |
| :--------   | :-------     | :-------------------------        |
| `title`     | `string`     | **Required**. Campaign title      |
| `startDate` | `YYYY-MM-DD` | **Required**. Campaign start date |
| `endDate`   | `YYYY-MM-DD` | **Optional**. Campaign end date   |

#### Get campaign

```http
  GET /v1/campaigns/${id}
```

| Parameter | Type     | Description                           |
| :-------- | :------- | :--------------------------------     |
| `id`      | `long`   | **Required**. Id of campaign to fetch |

#### Add N vouchers for a campaign

```http
  POST /v1/campaigns/${id}/vouchers?count=N
```

| Parameter    | Type     | Description                             |
| :--------    | :------- | :--------------------------------       |
| `id`         | `long`   | **Required**. Id of campaign            |
| `count`      | `int`    | **Required**. Number of vouchers to add |

#### Check voucher status

```http
  GET /v1/vouchers/${code}/check
```

| Parameter    | Type     | Description                             |
| :--------    | :------- | :--------------------------------       |
| `code`       | `UUID`   | **Required**. Unique voucher code       |

#### Redeem voucher

```http
  GET /v1/vouchers/${id}/redeem
```

| Parameter    | Type     | Description                         |
| :--------    | :------- | :--------------------------------   |
| `id`         | `long`   | **Required**. Voucher id            |

#### Send valid voucher for a specific campaignId

```http
  GET /v1/vouchers/send?campaignId=
```

| Parameter    | Type       | Description                         |
| :--------    | :-------   | :--------------------------------   |
| `campaignId` | `long`     | **Required**. Voucher ID            |


## Tech Stack

Java 17

Spring Boot

SpringDoc OpenAPI

Spring Testing and jUnit 5

PostgreSQL

Docker

GitHub Actions




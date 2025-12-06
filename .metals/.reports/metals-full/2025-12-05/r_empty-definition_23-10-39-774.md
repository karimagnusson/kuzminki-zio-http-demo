error id: file://<WORKSPACE>/src/main/scala/routes/TypeRoute.scala:`<none>`.
file://<WORKSPACE>/src/main/scala/routes/TypeRoute.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -zio/Int#
	 -zio/http/Int#
	 -zio/json/Int#
	 -models/Int#
	 -kuzminki/api/Int#
	 -kuzminki/fn/Int#
	 -Int#
	 -scala/Predef.Int#
offset: 760
uri: file://<WORKSPACE>/src/main/scala/routes/TypeRoute.scala
text:
```scala
package routes

import zio.*
import zio.http.*
import zio.json.*
import scala.language.implicitConversions
import models.*
import kuzminki.api.*
import kuzminki.api.given
import kuzminki.fn.*

// Type-safe queries with case classes using zio-schema for serialization.

object TypeRoute extends Responses {

  val country = Model.get[Country]
  val trip    = Model.get[Trip]

  // JSON codec definitions for types

  case class CountryType(code: String, name: String, population: Int)
  object CountryType {
    given JsonCodec[CountryType] = DeriveJsonCodec.gen[CountryType]
  }

  case class TripType(id: Long, cityId: Int, price: Int)
  object TripType {
    given JsonCodec[TripType] = DeriveJsonCodec.gen[TripType]
  }

  case class TripDataType(cityId: In@@t, price: Int)
  object TripDataType {
    given JsonCodec[TripDataType] = DeriveJsonCodec.gen[TripDataType]
  }

  case class TripPriceType(id: Long, price: Int)
  object TripPriceType {
    given JsonCodec[TripPriceType] = DeriveJsonCodec.gen[TripPriceType]
  }

  val routes = Routes(
    // SELECT with type-safe result mapping
    Method.GET / "type" / "select" / "country" / string("code") -> handler {
      (code: String, req: Request) =>
        sql
          .select(country)
          .cols3(t =>
            (
              t.code,
              t.name,
              t.population
            )
          )
          .where(_.code === code.toUpperCase)
          .runHeadType[CountryType] // map result to case class
          .map(rsp => Response.json(rsp.toJson))
    },

    // INSERT with type-safe input and output
    Method.POST / "type" / "insert" / "trip" -> handler { (req: Request) =>
      withJsonBody(req) { json =>

        val data = json.fromJson[TripDataType] match { // deserialize JSON to case class
          case Left(json)  => throw new Exception(s"Invalid data '$json'")
          case Right(data) => data
        }

        sql
          .insert(trip)
          .cols2(t =>
            (
              t.cityId,
              t.price
            )
          )
          .valuesType(data) // use case class for values
          .returning3(t =>
            (
              t.id,
              t.cityId,
              t.price
            )
          )
          .runHeadType[TripType] // map result to case class
          .map(rsp => Response.json(rsp.toJson))
      }
    },

    // UPDATE with type-safe input and output
    Method.PATCH / "type" / "update" / "trip" -> handler { (req: Request) =>
      withJsonBody(req) { json =>

        val data = json.fromJson[TripPriceType] match { // deserialize JSON to case class
          case Left(json)  => throw new Exception(s"Invalid data '$json'")
          case Right(data) => data
        }

        sql
          .update(trip)
          .set(_.price ==> data.price)
          .where(_.id === data.id)
          .returning3(t =>
            (
              t.id,
              t.cityId,
              t.price
            )
          )
          .runHeadType[TripType] // map result to case class
          .map(rsp => Response.json(rsp.toJson))
      }
    }
  )
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.
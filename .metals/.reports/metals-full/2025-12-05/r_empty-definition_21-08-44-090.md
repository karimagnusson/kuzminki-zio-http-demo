error id: file://<WORKSPACE>/src/main/scala/routes/CacheRoute.scala:`<none>`.
file://<WORKSPACE>/src/main/scala/routes/CacheRoute.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -zio/DeriveSchema.
	 -zio/http/DeriveSchema.
	 -zio/schema/DeriveSchema.
	 -models/DeriveSchema.
	 -kuzminki/api/DeriveSchema.
	 -DeriveSchema.
	 -scala/Predef.DeriveSchema.
offset: 508
uri: file://<WORKSPACE>/src/main/scala/routes/CacheRoute.scala
text:
```scala
package routes

import zio.*
import zio.http.*
import zio.schema.{DeriveSchema, Schema}
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import scala.language.implicitConversions
import models.*
import kuzminki.api.*
import kuzminki.api.given

// Cached queries

object CacheRoute extends Responses {

  val trip = Model.get[Trip]
  val city = Model.get[City]
  val country = Model.get[Country]

  case class TripInsert(city_id: Int, price: Int)
  object TripInsert {
    given Schema[TripInsert] = D@@eriveSchema.gen[TripInsert]
  }

  case class TripUpdate(id: Long, price: Int)
  object TripUpdate {
    given Schema[TripUpdate] = DeriveSchema.gen[TripUpdate]
  }

  case class TripDelete(id: Long)
  object TripDelete {
    given Schema[TripDelete] = DeriveSchema.gen[TripDelete]
  }

  val selectCountryStm = sql
    .select(country)
    .colsJson(t => Seq(
      t.code,
      t.name,
      t.continent,
      t.region
    ))
    .all
    .pickWhere1(_.code.use === Arg)
    .cache

  val selectJoinStm = sql
    .select(city, country)
    .colsJson(t => Seq(
      t.a.code,
      t.a.population,
      "city_name" -> t.a.name,
      "country_name" -> t.b.name,
      t.b.gnp,
      t.b.continent,
      t.b.region
    ))
    .joinOn(_.code, _.code)
    .where(t => Seq(
      t.b.continent === "Asia",
      t.b.gnp.isNotNull
    ))
    .orderBy(_.a.population.desc)
    .limit(5)
    .pickWhere2(t => (
      t.b.population.use >= Arg,
      t.b.gnp.use >= Arg
    ))
    .cache

  val insertTripStm = sql
    .insert(trip)
    .cols2(t => (
      t.cityId,
      t.price
    ))
    .returningJson(t => Seq(
      t.id,
      t.cityId,
      t.price
    ))
    .cache

  val updateTripStm = sql
    .update(trip)
    .pickSet1(_.price.use ==> Arg)
    .pickWhere1(_.id.use === Arg)
    .returningJson(t => Seq(
      t.id,
      t.cityId,
      t.price
    ))
    .cache

  val deleteTripStm = sql
    .delete(trip)
    .pickWhere1(_.id.use === Arg)
    .returningJson(t => Seq(
      t.id,
      t.cityId,
      t.price
    ))
    .cache

  val routes = Routes(
    Method.GET / "cache" / "select" / "country" / string("code") -> handler { (code: String, req: Request) =>
      selectCountryStm
        .runHeadOpt(code.toUpperCase)
        .map(jsonOpt(_))
    },

    Method.GET / "cache" / "join" / string("pop") / string("gnp") -> handler { (pop: String, gnp: String, req: Request) =>
      selectJoinStm
        .run(pop.toInt, BigDecimal(gnp))
        .map(jsonList(_))
    },

    Method.POST / "cache" / "insert" / "trip" -> handler { (req: Request) =>
      for {
        data <- req.body.to[TripInsert]
        result <- insertTripStm.runHead((data.city_id, data.price))
      } yield jsonObj(result)
    },

    Method.PATCH / "cache" / "update" / "trip" -> handler { (req: Request) =>
      for {
        data <- req.body.to[TripUpdate]
        result <- updateTripStm.runHeadOpt(data.price, data.id)
      } yield jsonOpt(result)
    },

    Method.DELETE / "cache" / "delete" / "trip" -> handler { (req: Request) =>
      for {
        data <- req.body.to[TripDelete]
        result <- deleteTripStm.runHeadOpt(data.id)
      } yield jsonOpt(result)
    }
  )
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.
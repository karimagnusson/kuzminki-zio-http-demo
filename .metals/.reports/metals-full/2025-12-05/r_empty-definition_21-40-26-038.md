error id: file://<WORKSPACE>/src/main/scala/routes/JsonbRoute.scala:`<none>`.
file://<WORKSPACE>/src/main/scala/routes/JsonbRoute.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -zio/DeriveSchema.
	 -zio/http/DeriveSchema.
	 -zio/schema/DeriveSchema.
	 -models/DeriveSchema.
	 -kuzminki/api/DeriveSchema.
	 -kuzminki/fn/DeriveSchema.
	 -DeriveSchema.
	 -scala/Predef.DeriveSchema.
offset: 650
uri: file://<WORKSPACE>/src/main/scala/routes/JsonbRoute.scala
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
import kuzminki.fn.*
import kuzminki.column.TypeCol

// Examples for jsonb field.

object JsonbRoute extends Responses {

  val countryData = Model.get[CountryData]

  case class PhoneData(code: String, phone: String)
  object PhoneData {
    given Schema[PhoneData] = DeriveSchema.gen[PhoneData]
  }

  case class CodeData(code: String)
  object CodeData {
    given Schema[CodeData] = DeriveSchema@@.gen[CodeData]
  }

  val routes = Routes(
    Method.GET / "jsonb" / "country" / string("code") -> handler { (code: String, req: Request) =>
      sql
        .select(countryData)
        .colsJson(t => Seq(
          t.uid,
          t.code,
          t.langs, // array field
          t.data   // jsonb field
        ))
        .where(_.code === code.toUpperCase)
        .runHeadOpt
        .map(jsonOptResponse(_))
    },

    Method.GET / "jsonb" / "capital" / string("name") -> handler { (name: String, req: Request) =>
      sql
        .select(countryData)
        .colsJson(t => Seq(
          t.uid,
          t.code,
          t.langs,
          (t.data || t.cities).as("data") // add cities to data
        ))
        .where(_.data -> "capital" ->> "name" === name)
        .runHeadOpt
        .map(jsonOptResponse(_))
    },

    Method.GET / "jsonb" / "city" / "population" -> handler { (req: Request) =>
      sql
        .select(countryData)
        .colsJson(t => Seq(
          t.uid,
          t.code,
          (t.data ->> "name").as("name"),
          (t.cities -> "cities" -> 0).as("largest_city")
        ))
        .where(t => (t.cities -> "cities" -> 0 ->> "population").isNotNull)
        .orderBy(t => (t.cities -> "cities" -> 0 ->> "population").asInt.desc)
        .limit(5)
        .run
        .map(jsonListResponse)
    },

    Method.GET / "jsonb" / "capital-avg" / string("cont") -> handler { (cont: String, req: Request) =>
      sql
        .select(countryData)
        .colsJson(t => Seq(
          Agg.avg((t.data #>> Seq("capital", "population")).asInt)
        ))
        .where(t => Seq(
          (t.data #>> Seq("capital", "population")).isNotNull,
          t.data ->> "continent" === cont
        ))
        .runHead
        .map(jsonObjResponse)
    },

    Method.PATCH / "jsonb" / "add" / "phone" -> handler { (req: Request) =>
      for {
        data <- req.body.to[PhoneData]
        result <- sql
          .update(countryData)
          .set(_.data += Jsonb("""{"phone": "%s"}""".format(data.phone))) // add "phone" to object
          .where(_.code === data.code)
          .returning1(_.data)
          .runHeadOpt
      } yield jsonOptResponse(result)
    },

    Method.PATCH / "jsonb" / "del" / "phone" -> handler { (req: Request) =>
      for {
        data <- req.body.to[CodeData]
        result <- sql
          .update(countryData)
          .set(_.data -= "phone") // remove "phone" from the object
          .where(_.code === data.code)
          .returning1(_.data)
          .runHeadOpt
      } yield jsonOptResponse(result)
    }
  )
}






















```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.
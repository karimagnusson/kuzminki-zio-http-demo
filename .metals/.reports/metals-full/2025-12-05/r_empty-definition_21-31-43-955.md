error id: file://<WORKSPACE>/src/main/scala/routes/DateRoute.scala:`<none>`.
file://<WORKSPACE>/src/main/scala/routes/DateRoute.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -zio.
	 -zio#
	 -zio().
	 -zio/http.
	 -zio/http#
	 -zio/http().
	 -models.
	 -models#
	 -models().
	 -kuzminki/api.
	 -kuzminki/api#
	 -kuzminki/api().
	 -kuzminki/fn.
	 -kuzminki/fn#
	 -kuzminki/fn().
	 -scala/Predef.
	 -scala/Predef#
	 -scala/Predef().
offset: 2133
uri: file://<WORKSPACE>/src/main/scala/routes/DateRoute.scala
text:
```scala
package routes

import zio.*
import zio.http.*
import scala.language.implicitConversions
import models.*
import kuzminki.api.*
import kuzminki.api.given
import kuzminki.fn.*

// Timestamp operations and date part extraction.

object DateRoute extends Responses {

  val btcPrice = Model.get[BtcPrice]

  val routes = Routes(
    // Extract date parts (year, day of year) and format timestamp
    Method.GET / "btc" / "hour" -> handler { (req: Request) =>
      sql
        .select(btcPrice)
        .colsJson(t => Seq(
          t.high.round(2),
          t.low.round(2),
          t.open.round(2),
          t.close.round(2),
          t.created.format("DD Mon YYYY HH24:MI")  // format timestamp
        ))
        .where(t => Seq(
          t.created.year === queryInt(req, "year"),  // extract year from timestamp
          t.created.doy === queryInt(req, "doy")     // extract day of year from timestamp
        ))
        .orderBy(_.created.asc)
        .run
        .map(jsonList(_))
    },

    // Aggregate data by quarter
    Method.GET / "btc" / "quarter" / "avg" -> handler { (req: Request) =>
      sql
        .select(btcPrice)
        .colsJson(t => Seq(
          "avg" -> Agg.avg(t.close).round(2),  // aggregate average
          "max" -> Agg.max(t.close).round(2),  // aggregate maximum
          "min" -> Agg.min(t.close).round(2)   // aggregate minimum
        ))
        .where(t => Seq(
          t.created.year === queryInt(req, "year"),
          t.created.quarter === queryInt(req, "quarter")  // extract quarter from timestamp
        ))
        .runHead
        .map(jsonObj(_))
    },

    // Extract multiple date parts (year, quarter, week)
    Method.GET / "btc" / "break" -> handler { (req: Request) =>
      sql
        .select(btcPrice)
        .colsJson(t => Seq(
          "price" -> t.high.round(2),
          "year" -> t.created.year,              // extract year
          "quarter" -> t.created.quarter,        // extract quarter
          "week" -> t.created.week,              // extract week number
          "date" -> t.created.format("DD Mon YYYY HH24:MI")
        ))
        .where(_.hi@@gh >= queryBigDecimal(req, "price"))
        .orderBy(_.high.asc)
        .limit(1)
        .runHeadOpt
        .map(jsonOpt(_))
    }
  )
}













```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.
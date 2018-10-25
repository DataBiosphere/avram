package org.broadinstitute.dsde.workbench.avram.graphql

import sangria.schema._

case class Picture(width: Int, height: Int, url: Option[String])

object ProductsExample {

  //schema
//  type Picture {
//  width: Int!
//    height: Int!
//    url: String
//}
//
//interface Identifiable {
//  id: String!
//}
//
//  type Product implements Identifiable {
//  id: String!
//  name: String!
//  description: String
//  picture(size: Int!): Picture
//}
//
//  type Query {
//  product(id: Int!): Product
//  products: [Product]
//}


  val PictureType = ObjectType(
    "Picture",
    "The product picture",

    fields[Unit, Picture](
      Field("width", IntType, resolve = _.value.width),
      Field("height", IntType, resolve = _.value.height),
      Field("url", OptionType(StringType),
        description = Some("Picture CDN URL"),
        resolve = _.value.url)))
}

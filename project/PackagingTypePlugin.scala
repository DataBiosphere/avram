import sbt._

// This plugin was added in order to fix the javax dependency issue described here:
// https://github.com/sbt/sbt/issues/3618#issuecomment-424924293
// Currently, this works if you sbt compile twice for some reason.
// This breaks the Circle CI test runs.
// TODO: Fix circle ci test failures related to this issue.
object PackagingTypePlugin extends AutoPlugin {
  override val buildSettings = {
    sys.props += "packaging.type" -> "jar"
    Nil
  }
}
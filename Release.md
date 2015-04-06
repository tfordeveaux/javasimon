# Overview #

You have to have your environment prepared according to: http://central.sonatype.org/pages/ossrh-guide.html

  1. first get ready, test it, run examples, `org.javasimon.examples.SimonConsoleJettyDemo`, `org.javasimon.demoapp.Start`
  1. update `readme.txt`, and `sVersion:"x.x"` in `javasimon-service.js` if needed,
  1. RELEASE it!

# With Maven and GIT #

Next steps are based on experiences with releasing the version 3.5.0. We will stick with manual release for better control. This is mostly based on [this section](http://central.sonatype.org/pages/apache-maven.html#performing-a-release-deployment) of their guide, but there are some differences, see lower.

## Release ##

Actual commands are a bit different than in Sonatype's guide (their guides are mostly too long and not always working for every case) For instnace, the profile name for release is different, this can be checked in parent POM `org.sonatype.oss:oss-parent:9`.

  1. `mvn versions:set -DnewVersion=3.5.0` (use actual version of course)
  1. `mvn version:commit`
  1. set JAVA\_HOME to desired JDK
  1. `mvn clean deploy -P sonatype-oss-release` (this will require your GPG passphrase and performs the upload to OSS staging repo)
  1. If there is any sub-project that is not mentioned as a module, you have to change its version manually (parent's version may be enough) and also release it manually (the same clean/deploy command, but from the subdirectory).

When all previous steps succeed, our artifacts are staged. If we want to promote them (Release) - of course, this can be done any time later after the other steps:

  1. To promote them, you may use [this section](http://central.sonatype.org/pages/apache-maven.html#releasing-the-deployment-to-the-central-repository) of their guide.
    1. Alternatively you can login into https://oss.sonatype.org/ and follow [these instructions](http://central.sonatype.org/pages/releasing-the-deployment.html).
  1. Now we can commit our non-snapshot version and move to snapshot version. But before that it's handy to do a few more steps.
    1. Put aside javadoc JAR, or you can run `build.sh` or `build.bat` to get API in `target/apidocs`. This way the version number in javadoc is non-snapshot, which is expected for published javadoc. I prefer zipping `target/apidocs`.
    1. Now it is time to commit the released version.
    1. `mvn version:set -DnewVersion=3.5.1-SNAPSHOT` (or any other you like)
    1. `mvn version:commit`
    1. Commit and push. This concludes the actions in the master branch.

## Javadoc ##

  1. `git checkout with-javadoc` We keep javadoc in separate branch.
  1. Create `api-3.5` (use actual version of course, 2 places are enough, we don't change API for patch versions anyway) and unzip javadoc inside. If you unzip javadoc from JAR, you may remove META-INF.
  1. Check it in the browser: `api-3.5/index.html`
  1. `git add api-3.5`
  1. Commit, push.
  1. After the push you can check URL: https://javasimon.googlecode.com/git-history/with-javadoc/api-3.5/index.html

Google code will show 404 for some time, you need to be patient here. It should be couple of minutes, not more than an hour.

## Distribution ZIP ##

Google code does not host new downloads anymore. Whoever want's ZIP distribution has to build it themselves.

## Wrap it up ##

  1. Add News on our main admin page.
  1. Add post on Java Simon's Google+ page.
  1. If it's revolutionary, propagate it even more.
  1. Use issue list for bugs/features as release notes.

# With Gradle and GIT #

TODO, planned for 4.x
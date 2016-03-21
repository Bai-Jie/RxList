import org.gradle.api.GradleException

class Version {

  String originalVersion
  String versionName
  int versionCode
  String status
  Date buildTime

  Version(String versionValue) {
    if (versionValue =~ /\d{2,}/) {
      throw new GradleException("versionValue(${versionValue}) contains number more than 9.")
    }
    buildTime = new Date()
    originalVersion = versionValue
    String originalVersionCodeString;
    if (originalVersion.endsWith('-SNAPSHOT')) {
      status = 'integration'
      originalVersionCodeString = originalVersion.
          substring(0, originalVersion.length() - '-SNAPSHOT'.length())
      versionName = versionValue
    } else {
      status = 'release'
      versionName = versionValue
      originalVersionCodeString = versionValue
    }
    versionCode = originalVersionCodeString.replace('.', '').toInteger();
  }

  String toString() {
    versionName
  }

}

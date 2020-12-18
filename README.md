# CSE2115 - Project

### Running

`gradle bootRun`

To run all microservices separately:

```
gradle eureka:bootRun
gradle requests:bootRun
gradle transactions:bootRun
gradle authentication:bootRun
gradle gateway:bootRun
```

Note that eureka should always be first, and when the gateway is last, it can get a reference for all other started
microservices from eureka.

### Testing

```
gradle test
```

To generate a coverage report:

```
gradle jacocoTestCoverageVerification
```

And

```
gradle jacocoTestReport
```

The coverage report is generated in: build/reports/jacoco/test/html, which does not get pushed to the repo. Open
index.html in your browser to see the report.

### Static analysis

```
gradle checkStyleMain
gradle checkStyleTest
gradle pmdMain
gradle pmdTest
```

### Notes

- If we change the name of the repo to something other than template, we should also edit the build.gradle file.

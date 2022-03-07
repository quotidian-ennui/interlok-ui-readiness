# interlok-ui-readiness

You hate the Interlok UI or you aren't using it in quite the same way that the interlok devs expect you to. That may mean your configuration vis-a-vis _config-project.json_ has gotten out of sync. You now want to start using the UI, but trying to import the project has royally screwed up your configuration.

This is an attempt to ameloriate your pain, so that you hate the UI less. It doesn't remove your pain, but if you have a lot of variables it should generate absolute xpaths that when you open up the configuration in the UI and re-save it should auto-fix itself.

Basically it does 2 things
- Turn a file with xinclude into a monolithic file
- Emit sufficient information that you can manually update `variableXpaths` inside an interlok-ui maintained config-project.json

## Quickstart...

- Assumes that you have replaced src/test/resources/adapter.xml with your appropriate copy.
    - Modify the "commandline" if that's not correct.
- Run gradle
```
$ ./gradlew [-PinterlokConfigFile=/path/to/file]
> Task :emitXpaths
{
  "variableXpaths" : {
    "/adapter/channel-list/channel[1]/auto-start" : "${channel.auto.start}",
    "/adapter/channel-list/channel[2]/auto-start" : "${channel.auto.start}",
    "/adapter/shared-components/connections/elastic-rest-connection/transport-url" : "${local.elastic.transporturl}",
    "/adapter/shared-components/services/service-list[1]/services/json-transform-service/mapping-spec/url" : "${adapter.base.url}/config/flatten-speedtest-output.json",
    "/adapter/shared-components/services/service-list[2]/services/standalone-producer/producer/index" : "${elastic.speedtest.index}",
    "/adapter/shared-components/services/service-list[1]/services/system-command-executor/command-builder/executable-path" : "${speedtest-cli.path}"
  }
}
```

- You should now be able to between `adapter.xml.monolithic` and the console output to manually retrofit a config-json that's suitable for the UI

## Additional info

- You can override the working directory by using `-PuiXpathWorkingDir=/some/path` but bear in mind at that point _interlokConfigPath_ is relative to your specified working directory.
- Compiled for Java8, not apropos of anything, It'll still work with Java 11.
- It has no direct dependencies on interlok
- If you attempt to validate the Variable XPaths in the UI, then it will not validate. This is a consequence of the positional XPath that is generated; the UI uses `unique-id` to qualify the xpath correctly. _However, because you aren't using the UI properly in the first place you probably haven't got useful unique-id's in place anyway_. You will be able to go through and resolve the warnings manually.
- It may not be ideal if you have inline JSON transforms that use variable substitutions. Technically it's _correct_ but there's no guarantee



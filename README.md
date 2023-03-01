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
    "/adapter/shared-components/services/service-list[unique-id=\"doSpeedtest\"]/services/json-transform-service[unique-id=\"flatten-json-output\"]/mapping-spec/url" : "${adapter.base.url}/config/flatten-speedtest-output.json",
    "/adapter/shared-components/services/service-list[unique-id=\"doSpeedtest\"]/services/system-command-executor[unique-id=\"execute-speedtest\"]/command-builder/executable-path" : "${speedtest-cli.path}",
    "/adapter/channel-list/channel[unique-id=\"ON_DEMAND\"]/auto-start" : "${channel.auto.start}",
    "/adapter/shared-components/services/service-list[unique-id=\"doElasticINDEX\"]/services/standalone-producer[unique-id=\"index-elastic-document\"]/producer[unique-id=\"gloomy-snyder\"]/index" : "${elastic.speedtest.index}",
    "/adapter/channel-list/channel[unique-id=\"SPEEDTEST_TO_ES\"]/auto-start" : "${channel.auto.start}",
    "/adapter/shared-components/connections/elastic-rest-connection[unique-id=\"local-elasticsearch\"]/transport-url" : "${local.elastic.transporturl}"
  }
}
```

- You should now be able to between `adapter.xml.monolithic` and the console output to manually retrofit a config-json that's suitable for the UI
    - In the above example one of the XPaths will not validate correctly, which is `gloomy-snyder` since this is _too granular_.
    - Once you have successfully opened the project in the UI then make sure you resolve the warnings on the variable-xpaths tab in the UI.

## Additional info

> Now requires Java 11 since Saxon-HE 12.0 && junit-pioneer 2.0.0 now require Java 11. Since Interlok 4.x is the latest stable, and it requires Java 11, it's probably a no brainer here.

- You can override the working directory by using `-PuiXpathWorkingDir=/some/path` but bear in mind at that point _interlokConfigPath_ is relative to your specified working directory.
- It has no direct dependencies on interlok
- If you attempt to validate the Variable XPaths in the UI, then it will not validate. This is a consequence of the positional XPath that is generated; the UI uses `unique-id` to qualify the xpath correctly. _However, because you aren't using the UI properly in the first place you probably haven't got useful unique-id's in place anyway_.
  - You will need to go through and resolve the warnings manually.
  - If the UI is not managing your XPaths then changes, manual or via the UI (such as re-ordering) may cause undefined behaviour.
- It may not be ideal if you have inline JSON transforms that use variable substitutions. Technically it's _correct_ but there's no guarantee



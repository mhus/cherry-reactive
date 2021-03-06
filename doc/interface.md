

== Internationalization in OSGi:

The OSGi IEngineFactory is loading the file 'etc/de.mhus.app.reactive.model.ui.IEngineFactory.properties' to
load nls parameters. The current parameters can be seen using the 'pnode-view' and 'pcase-view' commands
in karaf. Use the '-a' flag to print the current used parameters. Copy the parameters into the file and execute
'pengine cleanup' to reload the file content.

To define labels for different languages the language can be added to the parameters. Sample:

pnode-view -a ea0c363c-b0e1-4447-a017-897ac5a0daf0 me de

[...]

bpm://de.mhus.app.reactive.examples.simple1.S1Process:0.0.1/de.mhus.app.reactive.examples.simple1.S1Pool#description=This pool is used to test the current development
bpm://de.mhus.app.reactive.examples.simple1.S1Process:0.0.1/de.mhus.app.reactive.examples.simple1.S1Pool#displayName=Example Pool

You can create german labels like this

bpm://de.mhus.app.reactive.examples.simple1.S1Process:0.0.1/de.mhus.app.reactive.examples.simple1.S1Pool#description?de=Diese Pool wird benutzt um die aktuelle Entwicklung zu testen
bpm://de.mhus.app.reactive.examples.simple1.S1Process:0.0.1/de.mhus.app.reactive.examples.simple1.S1Pool#displayName?de=Beispiel-Pool

Then

pengine-tool cleanup

and

pcase-view a9dc47df-82e8-4c0d-bff7-de8166e8e377  me de-de

[...]

User        : me
Locale      : de
Display name: Beispiel-Pool

will print the new lables for german users.

== Display Names

ActivityDescription.displayName - Name of the activity
ActivityDescription.description - Description of the activity
ActivityDescription.indexDisplayNames[0..9] - Names of the indexes (if defined)


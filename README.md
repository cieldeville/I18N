# I18N
Internationalization API for use with CraftBukkit / Spigot

The I18N project aims to be an easy-to-use API for plugin developers to internationalize their plugins.
I18N takes away a lot of the repetitive and tedious work that must be done in order to have the right
translations in place and exposes a simple but powerful API that blends into the official Bukkit / Spigot
API nicely.

Currently, I18N is still under heavy development and API changes are still possible. Yet the following
features are currently working and tested under 1.9.2

- [x] Chat message translations
- [x] Actionbar translations
- [x] Title / Subtitle translations
- [x] Scoreboard translations (teams, objectives, scores)
- [x] Sign translations

I18N depends on ProtocolLib for its injection functionality.

## For server owners

If you are a server owner and you have been told that one of your plugin requires I18N to be installed in
order to work properly you can download the latest version from the Maven repository down below. Please note,
that I18N requires the latest version of ProtocolLib to be installed on your server.

### Configuration

Some parts of I18N can be configured and will be explained below. Note, that I18N uses SimpleConfig (see
https://github.com/BlackyPaw/SimpleConfig) for its configuration files instead of YAML. The SimpleConfig
format is in some ways less restrictive than the YAML format so it might be easier for you to edit.

Parameter				  	| Description
--------------------------- | ---------------------------
fallbackLocale				| The fallback locale to use for players whose locale could not be resolved (language code only)
useFallbackLocale			| Whether or not failed translation attempts should be re-attempted using the fallback locale configured above
allowUserTranslations		| If set to true the localizer used by this plugin itself will be allowed to lazy load user translations inside the translations folder
useNativeLanguageNames		| If set to true the native names of languages will be displayed. If set to false their english names will be displayed instead
defaultLocaleResolver		| The locale resolver to be used per default unless another plugin changes it programmatically (CONSTANT or DATABASE)
dbHost						| For use with DATABASE locale resolver: Hostname of MySQL instance
dbPort						| For use with DATABASE locale resolver: Port of MySQL instance
dbUser						| For use with DATABASE locale resolver: Username for MySQL authentication
dbPassword					| For use with DATABASE locale resolver: Password for MySQL authentication
dbName						| For use with DATABASE locale resolver: Name of Database for MySQL instance
dbPrefix					| For use with DATABASE locale resolver: Name prefix for MySQL tables

### Locale Resolvers

Unless you have installed a plugin that changes I18N's internal locale resolver programatically you can
configure the locale resolver you wish to use yourself. Locale resolvers provide a way of resolving and
/ or updating the preferred language of any player. Per default you can use the following constants for
the `defaultLocaleResolver` field inside I18N's configuration:

* CONSTANT - The fallback locale will be returned for each and every player
* DATABASE - Requires a MySQL database. Stores the preferred language of players inside a MySQL database. Might be slow.

### Commands

I18N adds a single command to your server which is the `/language` command. If no arguments are given
it will display your currently preferred language. If you specify an ISO639 language code as the command's
sole argument (e.g. en, de, fr, es, ...) it will try to change your preferred language. You will be notified
of the attempt's result in your chat afterwards.

### User Translations

When using the `/language` command you might have noticed that it can be displayed in different languages.
Per default I18N ships with translations for English and German, i.e. all text it prints out may be translated
into any one of these two languages. If you wish to add your own language to the plugin you can always check
out the .properties files inside the i18n-spigot/src/main/resources/translations folder of this repository's
source tree. If you are done with your translation you can save the file into the plugins/I18N/translations
folder of your server and name it appropriately (fr.properties, es.properties, ...). After you enable the 
`allowUserTranslations` option in your configuration you will see that the `/language` command will be
displayed in your own language, too.

## For plugin developers

If you would like to use I18N with your next plugin you can use the resources down below for further reference.

### Maven Repository

In order to simplify your life there is a Maven Repository from which you may retrieve the latest builds. Simply
add the following lines to your pom.xml:

```XML
<repositories>
	<repository>
        <id>blackypaw-repo</id>
        <name>BlackyPaw Public Repository</name>
        <url>http://repo.blackypaw.com/content/groups/public/</url>
    </repository>
</repositories>
```

### JavaDoc

You may find up-to-date JavaDoc pages here: https://www.blackypaw.com/docs/i18n/

### Example Usage

First of all you will need to create a translation storage object which will load and hold all translations
you intend to provide. I18N comes with a PropertyTranslationStorage implementation which allows you to store
your translations in .properties files named after the language's ISO code in one directory on disk. To
create such a translation storage you simply pass it the translation directory during creation:

```Java
TranslationStorage storage = new PropertyTranslationStorage( new File( "translations" ) );
```

After you created your respective translation storage you should load all languages you support. If you
prefer, you can use lazy loading instead, but for now we will use the manual approach:

```Java
try {
	storage.loadLanguage( Locale.ENGLISH );
	storage.loadLanguage( Locale.GERMAN );
	storage.loadLanguage( new Locale( "fr" ) );
	...
} catch ( IOException e ) {
	e.printStackTrace();
	// Failed to load one of the translations
}
```

After your translation storage is set up you will want to request a Localizer instance. A localizer allows
you to use I18N's translation injection functions which will simplify your work tremendeously (hopefully)
as we will explore later on.

```Java
Localizer localizer = I18NUtilities.createLocalizer( storage );
```

And that's it! Now you can use your new localizer instance to freely translate any message into any language
for all players without writing more than a single line of code.

### Translation Injection

After you have acquired your own localizer instance, why not use it to actually send a message? Imagine you had
a translation key in your translation storage which looks roughly like this:

```
[en.properties]
example-message=Hello, world!
[de.properties]
example-message=Hallo, Welt!
```

You could now send a hello world message to all players in their preferred language by just doing this:

```Java
Bukkit.broadcastMessage( localizer.inject( "example-message" ) );
```

All players should now see the text "Hello, world" or "Hallo, Welt!" in their chat window depending on
their preferred language. It is as easy as that. You can even have additional arguments inserted into
your translations. Make sure to check out the documentation for more information!

## License

The project is licensed under the BSD 3-Clause license found in the LICENSE file in this source tree's root
directory.

## Issues

Found a bug? Can't get it to work on your server? Feel free to create an issue here on this repository.

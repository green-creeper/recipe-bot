# recipe-bot

Telegram Bot for searching recipes by ingredients

you can perform search by list of ingridients, space or comma separated.
As result, bot sends one recipe. It's possible to exclude one of ingridients in result tapping appropriate button.
While ingridient is excluded - bot finds new recipe.

To setup bot follow this steps:
- Use BotFather (https://telegram.me/BotFather) bot to create bot
- Edit application.conf to insert bot username and token and database configuration
- Populate mongo database with data (use test dump)
- Ensure that database has indexes db.recipes.ensureIndex({"ingredients.name":"text"},{ default_language:"russian"});
- To run with test configuration use 'test' parameter. (java -jar recipebot.jar test)

Target audience of bot is russian-speaking, but it's the matter of database.

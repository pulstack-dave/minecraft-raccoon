package com.raccoon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrashItemTest {

    @Test
    public void testTrashRecipeJson() {
        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream("/data/raccoon/recipe/trash.json"),
                StandardCharsets.UTF_8)) {
            assertNotNull(reader, "trash.json recipe must exist");
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertEquals("minecraft:crafting_shapeless", json.get("type").getAsString());
            
            JsonArray ingredients = json.getAsJsonArray("ingredients");
            assertEquals(4, ingredients.size(), "Must contain exactly 4 ingredients (2 poisonous potatoes, 1 rotten flesh, 1 spider eye)");
            
            List<String> ingredientList = new ArrayList<>();
            ingredients.forEach(element -> ingredientList.add(element.getAsString()));
            
            long poisonousPotatoCount = ingredientList.stream().filter("minecraft:poisonous_potato"::equals).count();
            long rottenFleshCount = ingredientList.stream().filter("minecraft:rotten_flesh"::equals).count();
            long spiderEyeCount = ingredientList.stream().filter("minecraft:spider_eye"::equals).count();
            
            assertEquals(2, poisonousPotatoCount, "Recipe must contain 2 poisonous potatoes");
            assertEquals(1, rottenFleshCount, "Recipe must contain 1 rotten flesh");
            assertEquals(1, spiderEyeCount, "Recipe must contain 1 spider eye");
            
            JsonObject result = json.getAsJsonObject("result");
            assertEquals("raccoon:trash", result.get("id").getAsString());
            assertEquals(1, result.get("count").getAsInt());
        } catch (Exception e) {
            fail("Failed to read trash recipe JSON: " + e.getMessage());
        }
    }

    @Test
    public void testTrashModelAndItemDefinition() {
        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream("/assets/raccoon/items/trash.json"),
                StandardCharsets.UTF_8)) {
            assertNotNull(reader, "items/trash.json must exist");
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject modelObj = json.getAsJsonObject("model");
            assertNotNull(modelObj);
            assertEquals("raccoon:item/trash", modelObj.get("model").getAsString());
        } catch (Exception e) {
            fail("Failed to read items/trash.json: " + e.getMessage());
        }

        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream("/assets/raccoon/models/item/trash.json"),
                StandardCharsets.UTF_8)) {
            assertNotNull(reader, "models/item/trash.json must exist");
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertEquals("minecraft:item/generated", json.get("parent").getAsString());
            JsonObject textures = json.getAsJsonObject("textures");
            assertNotNull(textures);
            assertEquals("minecraft:item/suspicious_stew", textures.get("layer0").getAsString());
        } catch (Exception e) {
            fail("Failed to read models/item/trash.json: " + e.getMessage());
        }
    }

    @Test
    public void testTrashLangEntry() {
        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream("/assets/raccoon/lang/en_us.json"),
                StandardCharsets.UTF_8)) {
            assertNotNull(reader, "en_us.json must exist");
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertTrue(json.has("item.raccoon.trash"), "en_us.json must have item.raccoon.trash");
            assertEquals("Trash", json.get("item.raccoon.trash").getAsString());
        } catch (Exception e) {
            fail("Failed to read en_us.json: " + e.getMessage());
        }
    }
}

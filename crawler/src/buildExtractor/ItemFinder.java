package buildExtractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Anand on 12/13/2016.
 */
public class ItemFinder {

    public static int[] itemClassifier(String lastItemPurchased) {
        int[] returnArr = {0,0,0,0,0,0,0,0,0,0,0};
        final int CONSUMABLES = 0;
        final int ATTRIBUTES = 1;
        final int ARMAMENTS = 2;
        final int ARCANE = 3;
        final int COMMON = 4;
        final int SUPPORT = 5;
        final int CASTER = 6;
        final int WEAPONS = 7;
        final int ARMOR = 8;
        final int ARTIFACTS = 9;
        final int SECRET = 10;

        String[] Consumables = {"clarity", "faeriefire", "enchantedmango", "tango", "flask", "smokeofdeceit", "dust", "courier", "flyingcourier", "wardobserver", "wardsentry", "warddispenser", "tomeofknowledge", "bottle"};
        List<String> consumablesList = Arrays.asList(Consumables);
        String[] Attributes = {"branches", "gauntlets", "slippers", "mantle", "circlet", "beltofstrength", "bootsofelves", "robe", "ogreaxe", "bladeofalacrity", "staffofwizardry"};
        List<String> attributesList = Arrays.asList(Attributes);
        String[] Armaments = {"ringofprotection", "stoutshield", "quellingblade", "infusedraindrop", "blightstone", "orbofvenom", "bladesofattack", "chainmail", "quarterstaff", "helmofironwill", "broadsword", "claymore", "javelin", "mithrilhammer"};
        List<String> armamentsList = Arrays.asList(Armaments);
        String[] Arcane = {"windlace", "magicstick", "sobimask", "ringofregen", "boots", "gloves", "cloak", "ringofhealth", "voidstone", "gem", "lifesteal", "shadowamulet", "ghost", "blink"};
        List<String> arcaneList = Arrays.asList(Arcane);
        String[] Common = {"magicwand", "nulltalisman", "wraithband", "bracer", "poormansshield", "soulring", "phaseboots", "powertreads", "oblivionstaff", "pers", "handofmidas", "travelboots", "moonshard"};
        List<String> commonList = Arrays.asList(Common);
        String[] Support = {"ringofbasilius", "irontalon", "headdress", "buckler", "urnofshadows", "tranquilboots", "ringofaquila", "medallionofcourage", "arcaneboots", "ancientjanggo", "mekansm", "vladmir", "pipe", "guardiangreaves"};
        List<String> supportList = Arrays.asList(Support);
        String[] Caster = {"glimmercape", "forcestaff", "veilofdiscord", "aetherlens", "necronomicon", "dagon", "cyclone", "solarcrest", "rodofatos", "orchid", "ultimatescepter", "octarinecore", "referesher", "sheepstick"};
        List<String> casterList = Arrays.asList(Caster);
        String[] Weapons = {"lessercrit", "armlet", "invissword", "basher", "battlefury", "etherealblade", "silveredge", "radiance", "monkeykingbar", "greatercrit", "butterfly", "rapier", "abyssalblade", "bloodthorn"};
        List<String> weaponsList = Arrays.asList(Weapons);
        String[] Armor = {"hoodofdefiance", "vanguard", "blademail", "soulbooster", "crimsonguard", "blackkingbar", "lotusorb", "shivasguard", "bloodstone", "manta", "sphere", "assualt", "hurricanepike", "heart"};
        List<String> armorList = Arrays.asList(Armor);
        String[] Artifacts = {"maskofmadness", "helmofthedominator", "dragonlance", "echosabre", "desolator", "skadi", "yasha", "sange", "maelstrom", "mjollnir", "heavenshalberd", "sangeandyasha", "diffusalblade", "satanic"};
        List<String> artifactsList = Arrays.asList(Artifacts);
        String[] Secret = {"energybooster", "pointbooster", "vitalitybooster", "ultimateorb", "demonedge", "platemail", "talismanofevasion", "hyperstone", "mysticstaff", "reaver", "eagle", "relic"};
        List<String> secretList = Arrays.asList(Secret);
        String[] NoItem = {"", "tpscroll"};
        List<String> noItemList = Arrays.asList(NoItem);

        if (noItemList.contains(lastItemPurchased))
            return returnArr;

        if (lastItemPurchased.length() > 5) {
            lastItemPurchased = lastItemPurchased.substring(5, lastItemPurchased.length()).replaceAll("_", "").replaceAll("recipe", "");

            int index = -1;
            if (consumablesList.contains(lastItemPurchased))
                index = CONSUMABLES;
            else if (attributesList.contains(lastItemPurchased))
                index = ATTRIBUTES;
            else if (armamentsList.contains(lastItemPurchased))
                index = ARMAMENTS;
            else if (arcaneList.contains(lastItemPurchased))
                index = ARCANE;
            else if (commonList.contains(lastItemPurchased))
                index = COMMON;
            else if (casterList.contains(lastItemPurchased))
                index = CASTER;
            else if (armorList.contains(lastItemPurchased))
                index = ARMOR;
            else if (artifactsList.contains(lastItemPurchased))
                index = ARTIFACTS;
            else if (secretList.contains(lastItemPurchased))
                index = SECRET;
            else if (weaponsList.contains(lastItemPurchased))
                index = WEAPONS;
            else if (supportList.contains(lastItemPurchased))
                index = SUPPORT;

            if (index != -1) {
                returnArr[index] = 1;
            }
        }

        return returnArr;
    }

    public static JSONArray getItemClassified(String itemName) {
        int[] classes = itemClassifier(itemName);
        JSONArray classesArr = new JSONArray();
        for (int aClass : classes) {
            classesArr.put(aClass);
        }
        return classesArr;
    }


    public static void main(String[] args) {
        JSONArray arr = getItemClassified("item");
        JSONObject json = new JSONObject();
        try {
            json.put("classes", arr);
            System.out.println(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

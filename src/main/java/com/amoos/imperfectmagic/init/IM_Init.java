package com.amoos.imperfectmagic.init;


import com.amoos.imperfectmagic.ImperfectMagic;
import com.amoos.imperfectmagic.client.particle.LitParticle;
import com.amoos.imperfectmagic.client.particle.LitParticleOptions;
import com.amoos.imperfectmagic.client.particle.LitSga;
import com.amoos.imperfectmagic.client.particle.LitSgaOptions;
import com.amoos.imperfectmagic.item.CustomMagicNode;
import com.amoos.imperfectmagic.item.StaffMagicNode;
import com.amoos.imperfectmagic.item.Staff;
import com.amoos.imperfectmagic.utils.IM_Action;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class IM_Init {


    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ImperfectMagic.MODID);
    /*public enum Items{

        private final Item item;

        private Items(String name, Item item){
            this.item = item;
            ITEMS.register()
        }
    }*/
    /*
    * public enum Items{
        //epic
        VOID_EYE("void_eye",new StaffMagicNode(Rarity.EPIC, 200) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.voidEye(se, lv);}}),
        LIGHTNING_LINK("lightning_link", new StaffMagicNode(Rarity.EPIC, 160) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.lightningLinkFirst(se, lv);}}),
        BLOOMING_VOLCANO("blooming_volcano", new StaffMagicNode(Rarity.EPIC, 160) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.volcanoRange(se, 10, 4,10, lv);}}),
        //rare
        WINTER_FLOWER("winter_flower", new StaffMagicNode(Rarity.RARE, 100) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.iceRange(se, 8, 3, 8, lv);}}),

        CURSE_WATCH("curse_watch", new StaffMagicNode(Rarity.RARE, 80) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.curse(se, lv);}}),

        WIND_POCKET ("wind_pocket",new StaffMagicNode(Rarity.RARE, 10) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.wind(se, 10, 4, 10, lv);}}),
        PURGE ("purge",new StaffMagicNode(Rarity.RARE, 100) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.purgeRange(se, 5, 2, 5, lv);}}),
        //uncommon
        FIREBALL ("fireball",new StaffMagicNode(Rarity.UNCOMMON, 5) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return (IM_Action.fireball(se,3,lv) != null);}}),
        SONIC ("sonic",new StaffMagicNode(Rarity.UNCOMMON, 10) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.sonic(se,lv);}}),
        //staff
        STAFF ("amethyst_staff",Staff.STAFF);



        Items(String name, Item item){
            this.item = item;
            ITEMS.register(name, ()->item);
        }

        private final Item item;

        public Item get() {
            return item;
        }
    }*/


    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace


    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    //epic
    public static final RegistryObject<Item> VOID_EYE = ITEMS.register("void_eye", () -> new StaffMagicNode(Rarity.EPIC, 200) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.voidEye(se, lv);}});
    /*public static final RegistryObject<Item> LIGHTNING_LINK = ITEMS.register("lightning_eye", () -> new StaffMagicNode(Rarity.EPIC, 200) {
        @Override
        public boolean trig(LivingEntity se, ItemStack is) {
            boolean b = super.trig(se, is);
            if(b && getLV(is) >= MAX_LEVEL && se.getY() < -64){
                Player p = ((Player) se);
                p.getInventory().removeItem(is);
                p.getAbilities().flying = true;
                p.onUpdateAbilities();
                ItemHandlerHelper.giveItemToPlayer(p, new ItemStack(IM_Init.VOID_EYE.get()));
            }
            return b;
        }
        @Override
        public boolean trigBehavior(LivingEntity se, int lv) {
            return IM_Action.lightningLinkFirst(se, lv);
        }
    });*/
    public static final RegistryObject<Item> LIGHTNING_LINK = ITEMS.register("lightning_link", () -> new StaffMagicNode(Rarity.EPIC, 160) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.lightningLinkFirst(se, lv);}});
    public static final RegistryObject<Item> BLOOMING_VOLCANO = ITEMS.register("blooming_volcano", () -> new StaffMagicNode(Rarity.EPIC, 160) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.volcanoRange(se, 10, 4,10, lv);}});

    //rare
    public static final RegistryObject<Item> WINTER_FLOWER = ITEMS.register("winter_flower", () -> new StaffMagicNode(Rarity.RARE, 100) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.iceRange(se, 8, 3, 8, lv);}});
    public static final RegistryObject<Item> CURSE_WATCH = ITEMS.register("curse_watch", () -> new StaffMagicNode(Rarity.RARE, 80) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.curse(se, lv);}});

    public static final RegistryObject<Item> WIND_POCKET = ITEMS.register("wind_pocket", () -> new StaffMagicNode(Rarity.RARE, 10) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.wind(se, 10, 4, 10, lv);}});
    public static final RegistryObject<Item> PURGE = ITEMS.register("purge", () -> new StaffMagicNode(Rarity.RARE, 100) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.purgeRange(se, 5, 2, 5, lv);}});

    //commom
    public static final RegistryObject<Item> FIREBALL = ITEMS.register("fireball", () -> new StaffMagicNode(Rarity.UNCOMMON, 5) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return (IM_Action.fireball(se,3,lv) != null);}});
    public static final RegistryObject<Item> SONIC = ITEMS.register("sonic", () -> new StaffMagicNode(Rarity.UNCOMMON, 10) {@Override public boolean trigBehavior(LivingEntity se, int lv) {return IM_Action.sonic(se,lv);}});

    //staff
    public static final RegistryObject<Item> STAFF = ITEMS.register("amethyst_staff", () -> Staff.STAFF);
    //custom
    public static final RegistryObject<Item> UNCOMMON = ITEMS.register("uncommon",() -> new CustomMagicNode(Rarity.UNCOMMON));
    public static final RegistryObject<Item> RARE = ITEMS.register("rare", () -> new CustomMagicNode(Rarity.RARE));
    public static final RegistryObject<Item> EPIC = ITEMS.register("epic", () -> new CustomMagicNode(Rarity.EPIC));

    /*"example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));*/

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab


    /**
     * creative tab
     */

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ImperfectMagic.MODID);
    public static final RegistryObject<CreativeModeTab> IMPERFECT_MAGIC_TAB = CREATIVE_MODE_TABS.register("imperfect_magic_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.translatable("item_group.imperfect_magic.imperfect_magic_tab"))
            .icon(() -> STAFF.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(STAFF.get());

                output.accept(LIGHTNING_LINK.get());
                output.accept(BLOOMING_VOLCANO.get());

                output.accept(WINTER_FLOWER.get());
                output.accept(CURSE_WATCH.get());
                output.accept(WIND_POCKET.get());
                output.accept(PURGE.get());

                output.accept(FIREBALL.get());
                output.accept(SONIC.get());
                // Add the example item to the tab. For your own tabs, this method is preferred over the event
                //output.accept(IMInit.EXAMPLE_BLOCK_ITEM.get());
            }).build());

    /**
     * blocks
     */

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ImperfectMagic.MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "imperfect_magic" namespace
    // Creates a new Block with the id "imperfect_magic:example_block", combining the namespace and path
    //public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // Creates a new BlockItem with the id "imperfect_magic:example_block", combining the namespace and path
    //public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    /**
     * particles
     */
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ImperfectMagic.MODID);
    public static final RegistryObject<ParticleType<LitParticleOptions>> LIT = PARTICLES.register("lit_particle",  () -> new ParticleType<>(false, LitParticleOptions.DESERIALIZER) {
        public Codec<LitParticleOptions> codec() {
            return LitParticleOptions.CODEC;
        }
    });
    public static final RegistryObject<ParticleType<LitSgaOptions>> LIT_SGA = PARTICLES.register("lit_sga",  () -> new ParticleType<>(false, LitSgaOptions.DESERIALIZER) {
        public Codec<LitSgaOptions> codec() {return LitSgaOptions.CODEC;}
    });

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Particles {
        @SubscribeEvent
        public static void registerParticles(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(LIT.get(), LitParticle::provider);
            event.registerSpriteSet(LIT_SGA.get(), LitSga::provider);
        }
    }

    /**
     * gamerule
     */
    //public static final GameRules.Key<GameRules.IntegerValue> LIT_PARTICLE_VISUAL_SIZE = GameRules.register("litParticleVisualSize", GameRules.Category.MISC, GameRules.IntegerValue.create(2));


}

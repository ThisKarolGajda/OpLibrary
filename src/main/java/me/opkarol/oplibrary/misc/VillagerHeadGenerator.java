package me.opkarol.oplibrary.misc;

import me.opkarol.oplibrary.inventories.ItemBuilder;
import me.opkarol.oplibrary.tools.Heads;

public class VillagerHeadGenerator {

    public static ItemBuilder getRandomHead(String key) {
        if (key == null) {
            throw new RuntimeException("Key is null");
        }

        int hash = key.hashCode();
        VillagerHeadType[] values = VillagerHeadType.values();
        return values[Math.abs(hash) % values.length].getHead();
    }

    public static ItemBuilder getRandomHead() {
        VillagerHeadType[] values = VillagerHeadType.values();
        return values[(int) (Math.random() * values.length)].getHead();
    }

    public enum VillagerHeadType {
        FLETCHER_VILLAGER("d3dac7c3c1e6c9c707b855b8437b135f93f93f92a32d43008232df271f85bea"),
        KNIGHT_VILLAGER("11d9bbda9290701a70e16c45db71ca2e7916c852ee975b5b07d0be8b68009d84"),
        FARMER_VILLAGER("ef15fe4b7623c753393524f557b36ec81258a75daba159b39a6f800f7171a475"),
        ASSASSIN_VILLAGER("d583042b218842597cefe0aa1399c2d8e997e5c6d70337286cf982e5ce050342"),
        SUSPICIOUS_VILLAGER("76f4db68aa8bc3904183b997b99c29280dbf3d37f7b4126214ed3eb4c3387ddf"),
        DOCTOR_VILLAGER("7861767aeb5c87ab15d9a24dd4cae71e6d53356001e3545819aacfa792fc4e72"),
        DESERT_VILLAGER("a8a3523a7457c72c23588c16df36963da36c9f83ae2b1e5b8b6ec6d4675b10a8"),
        MAYOR_VILLAGER("26599cbb8868237e3d864bb128ac51a0ec4a5a85e241232ee3ed6b0afac9b5c7"),
        VILLAGER("126ec1ca185b47aad39f931db8b0a8500ded86a127a204886ed4b3783ad1775c"),
        ARMORED_VILLAGER("465f3f979781fda49cd2453e0bb17a2308695064cab7ff927907905775261e3a"),
        NATIVE_AMERICAN_VILLAGER("a8fdb22ba4842ff5f4bbdb383b244ff7a242b8c568f2be69d5b718adaf298bcc"),
        STRANGE_VILLAGER("a2ce9b42cb1f664d02c8ccda6155f5186adc09475c3769db20e6bdb84ee68468"),
        ;

        private final String texture;

        VillagerHeadType(String texture) {
            this.texture = texture;
        }

        public String getTexture() {
            return texture;
        }

        public ItemBuilder getHead() {
            return Heads.get(getTexture());
        }
    }
}

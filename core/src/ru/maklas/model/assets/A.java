package ru.maklas.model.assets;

import com.badlogic.gdx.utils.Array;

public class A {

    public static final ImageAssets     images;
    public static final SkinAssets      skins;

    private static Array<Asset> allAssets;

    static {
        images = new ImageAssets();
        skins = new SkinAssets();


        allAssets = new Array<>();
        allAssets.add(images);
        allAssets.add(skins);
    }

    public static Array<Asset> all() {
        return allAssets;
    }

}

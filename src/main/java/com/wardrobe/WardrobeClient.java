package com.wardrobe;

import com.wardrobe.auth.ElyAuthManager;
import com.wardrobe.client.community.CommunityLibrary;
import net.fabricmc.api.ClientModInitializer;

public class WardrobeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ElyAuthManager.init();
        CommunityLibrary.loadCommunityPresets();
    }
}

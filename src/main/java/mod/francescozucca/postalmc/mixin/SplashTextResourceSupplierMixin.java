package mod.francescozucca.postalmc.mixin;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {

    private static final String[] newSplashes = {
            "A foras sa NATO!",
            "Tempus Fides Pueris!",
            "BANK ANGLE BANK ANGLE BANK ANGLE"
    };

    @Shadow @Final private List<String> splashTexts;

    @Inject(method = "apply(Ljava/util/List;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("RETURN"))
    public void addNewSplashTexts(List<String> list, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci){
        //splashTexts.clear();
        splashTexts.addAll(List.of(newSplashes));
    }
}

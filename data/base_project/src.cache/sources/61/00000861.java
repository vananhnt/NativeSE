package android.media.effect.effects;

import android.filterpacks.imageproc.SepiaFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

/* loaded from: SepiaEffect.class */
public class SepiaEffect extends SingleFilterEffect {
    public SepiaEffect(EffectContext context, String name) {
        super(context, name, SepiaFilter.class, "image", "image", new Object[0]);
    }
}
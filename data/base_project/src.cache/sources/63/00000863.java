package android.media.effect.effects;

import android.filterpacks.imageproc.StraightenFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

/* loaded from: StraightenEffect.class */
public class StraightenEffect extends SingleFilterEffect {
    public StraightenEffect(EffectContext context, String name) {
        super(context, name, StraightenFilter.class, "image", "image", new Object[0]);
    }
}
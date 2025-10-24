package be.isach.ultracosmetics.paper;

import com.destroystokyo.paper.ClientOption;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Nameable;
import org.bukkit.entity.Player;

public class PaperSupportImpl implements PaperSupport {
    private final MiniMessage miniMessage;

    public PaperSupportImpl() {
        // Fail early if we're not actually on recent Paper
        ClientOption.ParticleVisibility.class.getName();

        // Copied from MessageManager. We can't touch UC's shaded version of adventure here because it will be relocated
        TagResolver.Builder tagBuilder = TagResolver.builder()
                .resolver(StandardTags.color())
                .resolver(StandardTags.decorations())
                .resolver(StandardTags.font())
                .resolver(StandardTags.gradient())
                .resolver(StandardTags.newline())
                .resolver(StandardTags.rainbow())
                .resolver(StandardTags.reset());
        this.miniMessage = MiniMessage.builder().tags(tagBuilder.build()).build();
    }

    @Override
    public boolean hasParticlesDisabled(Player player) {
        return player.getClientOption(ClientOption.PARTICLE_VISIBILITY) == ClientOption.ParticleVisibility.MINIMAL;
    }

    @Override
    public void setCustomName(Nameable nameable, String serializedComponent) {
        nameable.customName(miniMessage.deserialize(serializedComponent));
    }
}

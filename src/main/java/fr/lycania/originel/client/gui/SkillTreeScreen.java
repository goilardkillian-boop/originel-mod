package fr.lycania.originel.client.gui;

import de.teamlapen.vampirism.api.VampirismAPI;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.faction.HybridePlayer;
import fr.lycania.originel.network.ServerboundUnlockSkillPacket;
import fr.lycania.originel.skill.Branch;
import fr.lycania.originel.skill.Skill;
import fr.lycania.originel.skill.SkillRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Player-facing skill tree: lets the Hybride spend their own skill points
 * (earned passively - see HybrideLevelHandler) on any of the 13 skills,
 * instead of requiring a staff member to run /originel skill give. Reachable
 * via OriginelKeys.SKILL_TREE or by sneak-using the Carnet de Corvin (see
 * CarnetCorvinItem). Unlike SkillWheelScreen (a radial menu for USING
 * already-unlocked active skills), this is a plain grid of buttons - one
 * per Branch column - since browsing/comparing many locked and unlocked
 * skills at once doesn't fit the wheel's polar layout.
 */
public final class SkillTreeScreen extends Screen {

    private static final int COLUMN_WIDTH = 150;
    private static final int COLUMN_GAP = 10;
    private static final int ROW_HEIGHT = 22;
    private static final int ROW_GAP = 4;
    private static final int TOP_MARGIN = 44;

    private final Map<Branch, List<Skill>> byBranch = new EnumMap<>(Branch.class);
    private int lastSkillPoints = -1;
    private int lastUnlockedCount = -1;

    private SkillTreeScreen() {
        super(Component.translatable("gui.originel.skill_tree.title"));
        for (Skill skill : SkillRegistry.all().values()) {
            byBranch.computeIfAbsent(skill.branch(), b -> new ArrayList<>()).add(skill);
        }
    }

    public static void show() {
        Minecraft.getInstance().setScreen(new SkillTreeScreen());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        rebuild();
    }

    @Override
    public void tick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        HybridePlayer data = player.getData(HybrideAttachments.HYBRIDE_PLAYER);
        if (data.getSkillPoints() != lastSkillPoints || data.getUnlockedSkills().size() != lastUnlockedCount) {
            rebuild();
        }
    }

    private void rebuild() {
        clearWidgets();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        HybridePlayer data = player.getData(HybrideAttachments.HYBRIDE_PLAYER);
        lastSkillPoints = data.getSkillPoints();
        lastUnlockedCount = data.getUnlockedSkills().size();
        int currentLevel = VampirismAPI.factionPlayerHandler(player).getCurrentLevel(HybrideFaction.get());
        int maxLevel = HybrideFaction.get().getHighestReachableLevel();

        int totalWidth = Branch.values().length * COLUMN_WIDTH + (Branch.values().length - 1) * COLUMN_GAP;
        int startX = (width - totalWidth) / 2;

        Branch[] branches = Branch.values();
        for (int col = 0; col < branches.length; col++) {
            Branch branch = branches[col];
            List<Skill> skills = byBranch.getOrDefault(branch, List.of());
            int x = startX + col * (COLUMN_WIDTH + COLUMN_GAP);
            int y = TOP_MARGIN + 16;
            for (Skill skill : skills) {
                addSkillButton(skill, data, currentLevel, maxLevel, x, y);
                y += ROW_HEIGHT + ROW_GAP;
            }
        }
    }

    private void addSkillButton(Skill skill, HybridePlayer data, int currentLevel, int maxLevel, int x, int y) {
        boolean unlocked = data.hasSkill(skill.id());
        boolean levelOk = !skill.requiresMaxLevel() || currentLevel >= maxLevel;
        boolean affordable = data.getSkillPoints() >= skill.cost();
        boolean unlockable = !unlocked && levelOk && affordable;

        MutableComponent label = skill.displayName().copy()
                .append(Component.literal(" (" + skill.cost() + ")"));
        if (unlocked) {
            label = label.append(Component.translatable("gui.originel.skill_tree.unlocked_suffix"));
        }

        Button button = Button.builder(label, b -> {
                    PacketDistributor.sendToServer(new ServerboundUnlockSkillPacket(skill.id()));
                    b.active = false;
                })
                .bounds(x, y, COLUMN_WIDTH, ROW_HEIGHT)
                .tooltip(Tooltip.create(tooltipFor(skill, unlocked, levelOk, affordable, maxLevel)))
                .build();
        button.active = unlockable;
        addRenderableWidget(button);
    }

    private Component tooltipFor(Skill skill, boolean unlocked, boolean levelOk, boolean affordable, int maxLevel) {
        Component tooltip = skill.description();
        if (unlocked) {
            return tooltip;
        }
        if (!levelOk) {
            return tooltip.copy().append(Component.literal("\n")).append(
                    Component.translatable("gui.originel.skill_tree.requires_max_level", maxLevel));
        }
        if (!affordable) {
            return tooltip.copy().append(Component.literal("\n")).append(
                    Component.translatable("gui.originel.skill_tree.not_enough_points", skill.cost()));
        }
        return tooltip;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFFFF);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            HybridePlayer data = player.getData(HybrideAttachments.HYBRIDE_PLAYER);
            graphics.drawCenteredString(font, Component.translatable("gui.originel.skill_tree.points", data.getSkillPoints()),
                    width / 2, 26, 0xFFAAAAAA);
        }

        Branch[] branches = Branch.values();
        int totalWidth = branches.length * COLUMN_WIDTH + (branches.length - 1) * COLUMN_GAP;
        int startX = (width - totalWidth) / 2;
        for (int col = 0; col < branches.length; col++) {
            int x = startX + col * (COLUMN_WIDTH + COLUMN_GAP);
            graphics.drawCenteredString(font, Component.translatable("branch.originel." + branches[col].name().toLowerCase(Locale.ROOT)),
                    x + COLUMN_WIDTH / 2, TOP_MARGIN, 0xFFFFCC66);
        }
    }
}

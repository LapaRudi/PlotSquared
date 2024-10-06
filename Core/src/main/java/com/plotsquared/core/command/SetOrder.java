/*
 * PlotSquared, a land and world management plugin for Minecraft.
 * Copyright (C) IntellectualSites <https://intellectualsites.com>
 * Copyright (C) IntellectualSites team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.plotsquared.core.command;

import com.plotsquared.core.configuration.caption.StaticCaption;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.FlagParseException;
import com.plotsquared.core.plot.flag.GlobalFlagContainer;
import com.plotsquared.core.plot.flag.PlotFlag;
import com.plotsquared.core.plot.flag.implementations.OrderFlag;
import com.plotsquared.core.util.query.PlotQuery;

@CommandDeclaration(command = "setorder",
        permission = "plots.setorder",
        usage = "/plot setorder <count>",
        aliases = "order",
        category = CommandCategory.SETTINGS)
public class SetOrder extends SubCommand {

    @Override
    public boolean onCommand(final PlotPlayer<?> player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(StaticCaption.of("<dark_gray>[<gold>Plots<dark_gray>] <gold>Verwendung: <gray>/plot setorder <Zahl>"));
            return true;
        }

        int count;

        try {
            count = Integer.parseInt(args[0]);
        } catch (final NumberFormatException exception) {
            player.sendMessage(StaticCaption.of("<dark_gray>[<gold>Plots<dark_gray>] <red>Du musst eine Zahl angeben."));
            return true;
        }

        final Plot plot = player.getCurrentPlot();

        if (plot == null) {
            player.sendMessage(StaticCaption.of("<dark_gray>[<gold>Plots<dark_gray>] <red>Du befindest dich nicht auf einem " +
                    "Plot."));
            return true;
        }

        if (!plot.isOwner(player.getUUID())) {
            player.sendMessage(StaticCaption.of("<dark_gray>[<gold>Plots<dark_gray>] <red>Dieses Plot gehört dir nicht."));
            return true;
        }

        final PlotQuery query = PlotQuery.newQuery().ownedBy(player);
        final PlotFlag<?, ?> flag = GlobalFlagContainer.getInstance().getFlagErased(OrderFlag.class);

        if (count == 0) {
            plot.removeFlag(OrderFlag.class);
            player.sendMessage(StaticCaption.of("<dark_gray>[<gold>Plots<dark_gray>] <gray>Die Reihenfolge für dieses " +
                    "Grundstück wurde zurückgesetzt."));
            return true;
        }

        if (count < 1 || count > query.count()) {
            player.sendMessage(StaticCaption.of("<dark_gray>[<gold>Plots<dark_gray>] <red>Dieser Wert ist entweder zu groß " +
                    "oder zu klein."));
            return true;
        }

        if (query.asSet().stream().anyMatch(playerPlot -> playerPlot.getFlag(OrderFlag.class) == count)) {
            player.sendMessage(StaticCaption.of("<dark_gray>[<gold>Plots<dark_gray>] <red>Ein anderes deiner Grundstücke " +
                    "besitzt dieses Nummer bereits."));

            return true;
        }


        try {
            plot.setFlag(flag.parse(args[0]));
            player.sendMessage(StaticCaption.of("<dark_gray>[<gold>Plots<dark_gray>] <gray>Reihenfolge für dieses Grundstück " +
                    "auf <yellow>" + args[0] + "<gray> gesetzt."));

        } catch (final FlagParseException exception) {
            player.sendMessage(StaticCaption.of("<dark_gray>[<gold>Plots<dark_gray>] <red>Ungültige Eingabe."));
        }

        return true;
    }
}

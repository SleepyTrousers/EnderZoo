package crazypants.enderzoo.gen;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderzoo.gen.item.ExportManager;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ReloadConfigCommand implements ICommand {

  public static final String NAME = "reloadStructGen";
  
  private final List<String> names = new ArrayList<String>();
  
  public ReloadConfigCommand() {
    names.add(NAME);
    names.add("rSG");
  }
  
  @Override
  public int compareTo(Object o) {
    return 0;
  }

  @Override
  public String getCommandName() {  
    return NAME;
  }

  @Override
  public String getCommandUsage(ICommandSender p_71518_1_) {
    return NAME + " will reload the config for structure generation";
  }

  @Override
  public List getCommandAliases() {
    return names;
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {    
    StructureRegister.instance.reload();    
    ExportManager.instance.loadExportFolder();
    sender.addChatMessage(new ChatComponentText("Reloaded Structure Generation Configs"));
  }

  @Override
  public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
    return true;
  }

  @Override
  public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
    return null;
  }

  @Override
  public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
    return false;
  }

}

package crazypants.enderzoo.charge;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityPrimedCharge extends Entity {

//  private static final int CHARGE_ID_KEY = EntityDataManager.createKey(EntityPrimedCharge.class, );

  private static final DataParameter<Integer> CHARGE_ID_KEY = EntityDataManager.<Integer>createKey(EntityPrimedCharge.class, DataSerializers.VARINT);
  
  int fuse = 80;
  private EntityLivingBase chargePlacedBy;
  private ICharge charge;

  public EntityPrimedCharge(World world) {
    super(world);
    preventEntitySpawning = true;
    setSize(0.98F, 0.98F);    
  }

  public EntityPrimedCharge(ICharge charge, World world, double x, double y, double z, EntityLivingBase placedBy) {
    this(world);
    this.charge = charge;
    setPosition(x, y, z);

    float f = (float) (Math.random() * Math.PI * 2.0D);
    motionX = -((float) Math.sin(f)) * 0.02F;
    motionY = 0.20000000298023224D;
    motionZ = -((float) Math.cos(f)) * 0.02F;
    fuse = 80;
    prevPosX = x;
    prevPosY = y;
    prevPosZ = z;
    chargePlacedBy = placedBy;
    dataWatcher.set(CHARGE_ID_KEY, charge.getID());
  }
  

  public int getFuse() {
    return fuse;
  }

  public void setFuse(int fuse) {
    this.fuse = fuse;
  }

  @Override
  protected void entityInit() {
    this.dataWatcher.register(CHARGE_ID_KEY, Integer.valueOf(80));
  }

  @Override
  protected boolean canTriggerWalking() {
    return false;
  }

  @Override
  public boolean canBeCollidedWith() {
    return !isDead;
  }

  @Override
  public void onUpdate() {

    if(worldObj.isRemote && isDead) {
//      System.out.println("EntityPrimedCharge.onUpdate: Dead");
    }

    prevPosX = posX;
    prevPosY = posY;
    prevPosZ = posZ;
    motionY -= 0.03999999910593033D;
    moveEntity(motionX, motionY, motionZ);
    motionX *= 0.9800000190734863D;
    motionY *= 0.9800000190734863D;
    motionZ *= 0.9800000190734863D;

    if(onGround) {
      motionX *= 0.699999988079071D;
      motionZ *= 0.699999988079071D;
      motionY *= -0.5D;
    }

    if(fuse-- <= 0) {
      setDead();
      if(!worldObj.isRemote) {
        if(charge != null) {
          charge.explode(this);
        }
      }
    } else {
      worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D);
    }
  }


  @Override
  protected void writeEntityToNBT(NBTTagCompound root) {
    root.setByte("Fuse", (byte) fuse);
    if(charge != null) {
      root.setInteger("chargeID", charge.getID());
    }
  }

  @Override
  protected void readEntityFromNBT(NBTTagCompound root) {
    fuse = root.getByte("Fuse");
    if(root.hasKey("charge")) {
      int id = root.getInteger("chargeID");
      dataWatcher.set(CHARGE_ID_KEY, id);
      charge = ChargeRegister.instance.getCharge(id);
    }
  }

  public EntityLivingBase getPlacedBy() {
    return chargePlacedBy;
  }

  public Block getBlock() {
    int id = dataWatcher.get(CHARGE_ID_KEY);
    charge = ChargeRegister.instance.getCharge(id);
    if(charge != null) {
      return charge.getBlock();
    }
    return null;
  }
}
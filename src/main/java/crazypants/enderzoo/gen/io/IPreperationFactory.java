package crazypants.enderzoo.gen.io;

import com.google.gson.JsonObject;

import crazypants.enderzoo.gen.structure.preperation.ISitePreperation;

public interface IPreperationFactory extends IFactory {

  ISitePreperation createPreperation(String uid, JsonObject json);

}

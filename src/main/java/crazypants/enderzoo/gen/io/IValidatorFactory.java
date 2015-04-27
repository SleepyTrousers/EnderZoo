package crazypants.enderzoo.gen.io;

import com.google.gson.JsonObject;

import crazypants.enderzoo.gen.structure.rules.ILocationValidator;
import crazypants.enderzoo.gen.structure.rules.ISitePreperation;

public interface IValidatorFactory extends IFactory {

  ILocationValidator createValidator(String uid, JsonObject json);

}

package crazypants.enderzoo.gen.io;

import com.google.gson.JsonObject;

import crazypants.enderzoo.gen.structure.preperation.ISitePreperation;
import crazypants.enderzoo.gen.structure.validator.ILocationValidator;

public interface IValidatorFactory extends IFactory {

  ILocationValidator createValidator(String uid, JsonObject json);

}

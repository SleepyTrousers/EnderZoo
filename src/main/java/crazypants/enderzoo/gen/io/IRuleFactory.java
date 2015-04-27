package crazypants.enderzoo.gen.io;

import com.google.gson.JsonObject;

import crazypants.enderzoo.gen.structure.rules.ILocationSampler;
import crazypants.enderzoo.gen.structure.rules.ILocationValidator;
import crazypants.enderzoo.gen.structure.rules.ISitePreperation;

public interface IRuleFactory extends ISamplerFactory, IValidatorFactory, IPreperationFactory {

}

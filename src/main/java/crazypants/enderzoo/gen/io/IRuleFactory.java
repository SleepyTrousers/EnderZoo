package crazypants.enderzoo.gen.io;

import com.google.gson.JsonObject;

import crazypants.enderzoo.gen.structure.preperation.ISitePreperation;
import crazypants.enderzoo.gen.structure.sampler.ILocationSampler;
import crazypants.enderzoo.gen.structure.validator.ILocationValidator;

public interface IRuleFactory extends ISamplerFactory, IValidatorFactory, IPreperationFactory {

}

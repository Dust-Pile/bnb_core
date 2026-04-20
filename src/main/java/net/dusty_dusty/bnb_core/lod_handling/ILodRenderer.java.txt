package net.dusty_dusty.bnb_core.lod_handling;

import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShaderProgram;
import com.seibel.distanthorizons.core.render.RenderBufferHandler;
import com.seibel.distanthorizons.core.render.renderer.RenderParams;

public interface ILodRenderer {

    void dhds$renderLodPass(IDhApiShaderProgram shaderProgram, RenderBufferHandler lodBufferHandler, RenderParams renderEventParam, boolean opaquePass);

}

package project.android.imageprocessing.filter.colour;

import android.opengl.GLES20;
import project.android.imageprocessing.filter.BasicFilter;

public class HighlightShadowFilter extends BasicFilter {
	private static final String UNIFORM_HIGHLIGHT = "u_Highlight";
	private static final String UNIFORM_SHADOW = "u_Shadow";
	
	private int highlightHandle;
	private int shadowHandle;
	private float highlight;
	private float shadow;
	
	public HighlightShadowFilter(float highlight, float shadow) {
		this.highlight = highlight;
		this.shadow = shadow;
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		highlightHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_HIGHLIGHT);
		shadowHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_SHADOW);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1f(highlightHandle, highlight);
		GLES20.glUniform1f(shadowHandle, shadow);
	}

	@Override
	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n" 
				+"uniform float "+UNIFORM_HIGHLIGHT+";\n"
				+"uniform float "+UNIFORM_SHADOW+";\n"
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				+"const vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);\n"
				
		  		+ "void main(){\n"
		  		+ "   vec4 texColour = texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+");\n"
		  		+ "   float luminance = dot(texColour.rgb, luminanceWeighting);\n"
		  		+ "   float s = clamp((pow(luminance, 1.0/("+UNIFORM_SHADOW+"+1.0)) + (-0.76)*pow(luminance, 2.0/("+UNIFORM_SHADOW+"+1.0))) - luminance, 0.0, 1.0);\n"
		  		+ "   float h = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-"+UNIFORM_HIGHLIGHT+")) + (-0.8)*pow(1.0-luminance, 2.0/(2.0-"+UNIFORM_HIGHLIGHT+")))) - luminance, -1.0, 0.0);\n"
		  		+ "   vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + s + h) - 0.0) * ((texColour.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));\n"
		  		+ "   gl_FragColor = vec4(result, texColour.a);\n"
		  		+ "}\n";		
	}

}
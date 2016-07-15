package com.seu.magicfilter.filter.helper;

/**
 * Created by kouseishouganzhoushizenminamixianxianjoushinseigentanleijou on 16/7/14.
 */
import android.util.Log;
import android.view.View;

import com.seu.magicfilter.filter.advanced.MagicImageAdjustFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageBrightnessFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageContrastFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageExposureFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageHueFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageSaturationFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageSharpenFilter;
import com.seu.magicfilter.widget.MagicImageView;

/**
 *
 * idea from  jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools.FilterAdjuster
 */
public class MagicFilterAdjuster {
    private final Adjuster<? extends GPUImageFilter> adjuster;

    public MagicFilterAdjuster(final GPUImageFilter filter) {
        if (filter instanceof GPUImageSharpenFilter) {
            adjuster = new SharpnessAdjuster().filter(filter);
        } else if (filter instanceof GPUImageContrastFilter) {
            adjuster = new ContrastAdjuster().filter(filter);
        } else if (filter instanceof GPUImageHueFilter) {
            adjuster = new HueAdjuster().filter(filter);
        } else if (filter instanceof GPUImageSaturationFilter) {
            adjuster = new SaturationAdjuster().filter(filter);
        } else if (filter instanceof GPUImageExposureFilter) {
            adjuster = new ExposureAdjuster().filter(filter);
        } else if (filter instanceof GPUImageBrightnessFilter) {
            adjuster = new BrightnessAdjuster().filter(filter);
        } else if (filter instanceof MagicImageAdjustFilter) {
            adjuster = new ImageAdjustAdjuster().filter(filter);
        } else {
            adjuster = null;
        }
    }

    public boolean canAdjust() {
        return adjuster != null;
    }

    public void adjust(final int percentage) {
        if (adjuster != null) {
            adjuster.adjust(percentage);
        }
    }

    public void adjust(final int percentage,final MagicFilterType type) {
        if (adjuster != null) {
            adjuster.adjust(percentage, type);
        }
    }

    private abstract class Adjuster<T extends GPUImageFilter> {
        private T filter;

        @SuppressWarnings("unchecked")
        public Adjuster<T> filter(final GPUImageFilter filter) {
            this.filter = (T) filter;
            return this;
        }

        public T getFilter() {
            return filter;
        }

        public abstract void adjust(int percentage);
        public void adjust(int percentage, MagicFilterType type) {
            adjust(percentage);
        }

        protected float range(final int percentage, final float start, final float end) {
            return (end - start) * percentage / 100.0f + start;
        }

        protected int range(final int percentage, final int start, final int end) {
            return (end - start) * percentage / 100 + start;
        }
    }

    private class SharpnessAdjuster extends Adjuster<GPUImageSharpenFilter> {

        @Override
        public void adjust(final int percentage) {
            getFilter().setSharpness(range(percentage, -4.0f, 4.0f));
        }
    }

    private class HueAdjuster extends Adjuster<GPUImageHueFilter> {

        @Override
        public void adjust(final int percentage) {
            getFilter().setHue(range(percentage, 0.0f, 360.0f));
        }
    }

    private class ContrastAdjuster extends Adjuster<GPUImageContrastFilter> {

        @Override
        public void adjust(final int percentage) {
            getFilter().setContrast(range(percentage, 0.0f, 4.0f));
        }
    }

    private class BrightnessAdjuster extends Adjuster<GPUImageBrightnessFilter> {

        @Override
        public void adjust(final int percentage) {
            getFilter().setBrightness(range(percentage, -0.5f, 0.5f));
        }
    }

    private class SaturationAdjuster extends Adjuster<GPUImageSaturationFilter> {

        @Override
        public void adjust(final int percentage) {
            getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
        }
    }

    private class ExposureAdjuster extends Adjuster<GPUImageExposureFilter> {

        @Override
        public void adjust(final int percentage) {
            getFilter().setExposure(range(percentage, -2.0f, 2.0f));
        }
    }

    private class ImageAdjustAdjuster extends Adjuster<MagicImageAdjustFilter> {

        @Override
        public void adjust(final int percentage) {

        }

        public void adjust(final int percentage, MagicFilterType type) {
            //GPUImageFilter filter = MagicFilterFactory.initFilters(type);
            switch (type) {
                case CONTRAST:
                    GPUImageContrastFilter gpuImageContrastFilter = new GPUImageContrastFilter();
                    gpuImageContrastFilter.init();
                    gpuImageContrastFilter.setContrast(range(percentage, 0.0f, 4.0f));
                    //GPUImageContrastFilter gpuImageContrastFilter = new GPUImageContrastFilter(range(percentage, 0.0f, 4.0f));
                    //gpuImageContrastFilter.setContrast(range(percentage, 0.0f, 4.0f));
                    break;
                case SHARPEN:
                    //getFilter().setSharpness(range(percentage, -4.0f, 4.0f));
                    GPUImageSharpenFilter gpuImageSharpenFilter = new GPUImageSharpenFilter(range(percentage, -4.0f, 4.0f));
                    break;
                case SATURATION:
                    //getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
                    GPUImageSaturationFilter gpuImageSaturationFilter = new GPUImageSaturationFilter(range(percentage, 0.0f, 2.0f));
                    break;
                case EXPOSURE:
                    //getFilter().setExposure(range(percentage, -2.0f, 2.0f));
                    GPUImageExposureFilter gpuImageExposureFilter = new GPUImageExposureFilter(range(percentage, -2.0f, 2.0f));
                    break;
                case BRIGHTNESS:
                    //getFilter().setBrightness(range(percentage, -0.5f, 0.5f));
                    GPUImageBrightnessFilter gpuImageBrightnessFilter = new GPUImageBrightnessFilter(range(percentage, -0.5f, 0.5f));
                    break;
                case HUE:
                    //getFilter().setHue(range(percentage, 0.0f, 360.0f));
                    GPUImageHueFilter gpuImageHueFilter = new GPUImageHueFilter(range(percentage, 0.0f, 360.0f));
                    break;
                default:
                    break;
            }
        }
    }
}

package com.gzd.shadowprogressimageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author GZDong
 * @date 2021/3/24
 */

public class ShadowProgressImageView extends AppCompatImageView {

    /**
     * 每一个刻度为125，由1000/8获得
     * Each scale is 125, obtained by 1000/8
     */
    private final static int PER_SCALE = 125;
    private int progress;
    private float perX, perY = 0f;
    private PathNode startPoint = new PathNode();
    private List<PathNode> nodes = new ArrayList<>();
    private boolean hasLoadNodes;
    private Path path = new Path();
    private Paint paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ShadowProgressImageView(@NonNull Context context) {
        this(context, null);
    }

    public ShadowProgressImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowProgressImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paintFill.setColor(ContextCompat.getColor(context, R.color.colorShadow));
        paintFill.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (progress > 0) {
            if (perX == 0f) {
                perX = getWidth() / (2f * PER_SCALE);
            }
            if (perY == 0f) {
                perY = getHeight() / (2f * PER_SCALE);
            }
            configNodes();
            configStartPoint();
            configPath();
            canvas.drawPath(path, paintFill);
        }
    }

    /**
     * 统计所有节点
     * Count all nodes
     */
    private void configNodes() {
        if (!hasLoadNodes) {
            nodes.add(new PathNode(getWidth(), 0, PER_SCALE));
            nodes.add(new PathNode(getWidth(), getHeight() / 2f, 2 * PER_SCALE));
            nodes.add(new PathNode(getWidth(), getHeight(), 3 * PER_SCALE));
            nodes.add(new PathNode(getWidth() / 2f, getHeight(), 4 * PER_SCALE));
            nodes.add(new PathNode(0, getHeight(), 5 * PER_SCALE));
            nodes.add(new PathNode(0, getHeight() / 2f, 6 * PER_SCALE));
            nodes.add(new PathNode(0, 0, 7 * PER_SCALE));
            nodes.add(new PathNode(getWidth() / 2f, 0, 8 * PER_SCALE));
            hasLoadNodes = true;
        }
    }

    /**
     * 配置第一个节点
     * Configure the first node
     */
    private void configStartPoint() {
        int pro = progress % PER_SCALE == 0 ? PER_SCALE : progress % PER_SCALE;
        float xPro = pro * perX;
        float yPro = pro * perY;
        if (progress <= PER_SCALE) {
            startPoint.setNode(getWidth() / 2f + xPro, 0, progress);
        } else if (progress <= 2 * PER_SCALE) {
            startPoint.setNode(getWidth(), yPro, progress);
        } else if (progress <= 3 * PER_SCALE) {
            startPoint.setNode(getWidth(), getHeight() / 2f + yPro, progress);
        } else if (progress <= 4 * PER_SCALE) {
            startPoint.setNode(getWidth() - xPro, getHeight(), progress);
        } else if (progress <= 5 * PER_SCALE) {
            startPoint.setNode(getWidth() / 2f - xPro, getHeight(), progress);
        } else if (progress <= 6 * PER_SCALE) {
            startPoint.setNode(0, getHeight() - yPro, progress);
        } else if (progress <= 7 * PER_SCALE) {
            startPoint.setNode(0, getHeight() / 2f - yPro, progress);
        } else if (progress < 8 * PER_SCALE) {
            startPoint.setNode(xPro, 0, progress);
        } else {
            progress = 0;
            invalidate();
        }
    }

    private void configPath() {
        path.reset();
        path.moveTo(getWidth() / 2f, getHeight() / 2f);
        path.lineTo(startPoint.x, startPoint.y);
        for (PathNode node : nodes) {
            if (node.weight > startPoint.weight) {
                path.lineTo(node.x, node.y);
            }
        }
        path.close();
    }

    /**
     * 设置进度 0-100
     * Setting progress 0-100
     *
     * @param progress 这里乘以10，方便计算，因为1000除以8没有小数
     *                 Multiplying by 10 here is convenient for calculation,
     *                 because there is no decimal when dividing 1000 by 8
     */
    public void setProgress(int progress) {
        this.progress = progress * 10;
        invalidate();
    }

    /**
     * 获取进度 0-100
     * Get progress 0-100
     *
     * @return 这里除以10，因为{@link ShadowProgressImageView#setProgress(int)}乘以10
     *         Divide by 10 here because {@link ShadowProgressImageView#setProgress(int)}
     *         is multiplied by 10.
     */
    public int getProgress() {
        return progress / 10;
    }

    /**
     * 储存Path需要走过的节点.
     * weight代表权重，当大于进度progress时才加入Path
     * Store the nodes that the Path needs to traverse.
     * Nodes are added to Path when it's weight is greater than progress
     */
    private static class PathNode {
        private float x;
        private float y;
        private int weight;

        public PathNode() {

        }

        public PathNode(float x, float y, int weight) {
            this.x = x;
            this.y = y;
            this.weight = weight;
        }

        public void setNode(float x, float y, int weight) {
            this.x = x;
            this.y = y;
            this.weight = weight;
        }
    }
}

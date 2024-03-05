package android.animation;

import android.animation.Animator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: AnimatorSet.class */
public final class AnimatorSet extends Animator {
    private ArrayList<Animator> mPlayingSet = new ArrayList<>();
    private HashMap<Animator, Node> mNodeMap = new HashMap<>();
    private ArrayList<Node> mNodes = new ArrayList<>();
    private ArrayList<Node> mSortedNodes = new ArrayList<>();
    private boolean mNeedsSort = true;
    private AnimatorSetListener mSetListener = null;
    boolean mTerminated = false;
    private boolean mStarted = false;
    private long mStartDelay = 0;
    private ValueAnimator mDelayAnim = null;
    private long mDuration = -1;
    private TimeInterpolator mInterpolator = null;

    public void playTogether(Animator... items) {
        if (items != null) {
            this.mNeedsSort = true;
            Builder builder = play(items[0]);
            for (int i = 1; i < items.length; i++) {
                builder.with(items[i]);
            }
        }
    }

    public void playTogether(Collection<Animator> items) {
        if (items != null && items.size() > 0) {
            this.mNeedsSort = true;
            Builder builder = null;
            for (Animator anim : items) {
                if (builder == null) {
                    builder = play(anim);
                } else {
                    builder.with(anim);
                }
            }
        }
    }

    public void playSequentially(Animator... items) {
        if (items != null) {
            this.mNeedsSort = true;
            if (items.length == 1) {
                play(items[0]);
                return;
            }
            for (int i = 0; i < items.length - 1; i++) {
                play(items[i]).before(items[i + 1]);
            }
        }
    }

    public void playSequentially(List<Animator> items) {
        if (items != null && items.size() > 0) {
            this.mNeedsSort = true;
            if (items.size() == 1) {
                play(items.get(0));
                return;
            }
            for (int i = 0; i < items.size() - 1; i++) {
                play(items.get(i)).before(items.get(i + 1));
            }
        }
    }

    public ArrayList<Animator> getChildAnimations() {
        ArrayList<Animator> childList = new ArrayList<>();
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            Node node = i$.next();
            childList.add(node.animation);
        }
        return childList;
    }

    @Override // android.animation.Animator
    public void setTarget(Object target) {
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            Node node = i$.next();
            Animator animation = node.animation;
            if (animation instanceof AnimatorSet) {
                ((AnimatorSet) animation).setTarget(target);
            } else if (animation instanceof ObjectAnimator) {
                ((ObjectAnimator) animation).setTarget(target);
            }
        }
    }

    @Override // android.animation.Animator
    public void setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    @Override // android.animation.Animator
    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    public Builder play(Animator anim) {
        if (anim != null) {
            this.mNeedsSort = true;
            return new Builder(anim);
        }
        return null;
    }

    @Override // android.animation.Animator
    public void cancel() {
        this.mTerminated = true;
        if (isStarted()) {
            ArrayList<Animator.AnimatorListener> tmpListeners = null;
            if (this.mListeners != null) {
                tmpListeners = (ArrayList) this.mListeners.clone();
                Iterator i$ = tmpListeners.iterator();
                while (i$.hasNext()) {
                    Animator.AnimatorListener listener = i$.next();
                    listener.onAnimationCancel(this);
                }
            }
            if (this.mDelayAnim != null && this.mDelayAnim.isRunning()) {
                this.mDelayAnim.cancel();
            } else if (this.mSortedNodes.size() > 0) {
                Iterator i$2 = this.mSortedNodes.iterator();
                while (i$2.hasNext()) {
                    Node node = i$2.next();
                    node.animation.cancel();
                }
            }
            if (tmpListeners != null) {
                Iterator i$3 = tmpListeners.iterator();
                while (i$3.hasNext()) {
                    Animator.AnimatorListener listener2 = i$3.next();
                    listener2.onAnimationEnd(this);
                }
            }
            this.mStarted = false;
        }
    }

    @Override // android.animation.Animator
    public void end() {
        this.mTerminated = true;
        if (isStarted()) {
            if (this.mSortedNodes.size() != this.mNodes.size()) {
                sortNodes();
                Iterator i$ = this.mSortedNodes.iterator();
                while (i$.hasNext()) {
                    Node node = i$.next();
                    if (this.mSetListener == null) {
                        this.mSetListener = new AnimatorSetListener(this);
                    }
                    node.animation.addListener(this.mSetListener);
                }
            }
            if (this.mDelayAnim != null) {
                this.mDelayAnim.cancel();
            }
            if (this.mSortedNodes.size() > 0) {
                Iterator i$2 = this.mSortedNodes.iterator();
                while (i$2.hasNext()) {
                    Node node2 = i$2.next();
                    node2.animation.end();
                }
            }
            if (this.mListeners != null) {
                ArrayList<Animator.AnimatorListener> tmpListeners = (ArrayList) this.mListeners.clone();
                Iterator i$3 = tmpListeners.iterator();
                while (i$3.hasNext()) {
                    Animator.AnimatorListener listener = i$3.next();
                    listener.onAnimationEnd(this);
                }
            }
            this.mStarted = false;
        }
    }

    @Override // android.animation.Animator
    public boolean isRunning() {
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            Node node = i$.next();
            if (node.animation.isRunning()) {
                return true;
            }
        }
        return false;
    }

    @Override // android.animation.Animator
    public boolean isStarted() {
        return this.mStarted;
    }

    @Override // android.animation.Animator
    public long getStartDelay() {
        return this.mStartDelay;
    }

    @Override // android.animation.Animator
    public void setStartDelay(long startDelay) {
        this.mStartDelay = startDelay;
    }

    @Override // android.animation.Animator
    public long getDuration() {
        return this.mDuration;
    }

    @Override // android.animation.Animator
    public AnimatorSet setDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("duration must be a value of zero or greater");
        }
        this.mDuration = duration;
        return this;
    }

    @Override // android.animation.Animator
    public void setupStartValues() {
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            Node node = i$.next();
            node.animation.setupStartValues();
        }
    }

    @Override // android.animation.Animator
    public void setupEndValues() {
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            Node node = i$.next();
            node.animation.setupEndValues();
        }
    }

    @Override // android.animation.Animator
    public void pause() {
        boolean previouslyPaused = this.mPaused;
        super.pause();
        if (!previouslyPaused && this.mPaused) {
            if (this.mDelayAnim != null) {
                this.mDelayAnim.pause();
                return;
            }
            Iterator i$ = this.mNodes.iterator();
            while (i$.hasNext()) {
                Node node = i$.next();
                node.animation.pause();
            }
        }
    }

    @Override // android.animation.Animator
    public void resume() {
        boolean previouslyPaused = this.mPaused;
        super.resume();
        if (previouslyPaused && !this.mPaused) {
            if (this.mDelayAnim != null) {
                this.mDelayAnim.resume();
                return;
            }
            Iterator i$ = this.mNodes.iterator();
            while (i$.hasNext()) {
                Node node = i$.next();
                node.animation.resume();
            }
        }
    }

    @Override // android.animation.Animator
    public void start() {
        this.mTerminated = false;
        this.mStarted = true;
        this.mPaused = false;
        if (this.mDuration >= 0) {
            Iterator i$ = this.mNodes.iterator();
            while (i$.hasNext()) {
                i$.next().animation.setDuration(this.mDuration);
            }
        }
        if (this.mInterpolator != null) {
            Iterator i$2 = this.mNodes.iterator();
            while (i$2.hasNext()) {
                i$2.next().animation.setInterpolator(this.mInterpolator);
            }
        }
        sortNodes();
        int numSortedNodes = this.mSortedNodes.size();
        for (int i = 0; i < numSortedNodes; i++) {
            Node node = this.mSortedNodes.get(i);
            ArrayList<Animator.AnimatorListener> oldListeners = node.animation.getListeners();
            if (oldListeners != null && oldListeners.size() > 0) {
                ArrayList<Animator.AnimatorListener> clonedListeners = new ArrayList<>(oldListeners);
                Iterator i$3 = clonedListeners.iterator();
                while (i$3.hasNext()) {
                    Animator.AnimatorListener listener = i$3.next();
                    if ((listener instanceof DependencyListener) || (listener instanceof AnimatorSetListener)) {
                        node.animation.removeListener(listener);
                    }
                }
            }
        }
        final ArrayList<Node> nodesToStart = new ArrayList<>();
        for (int i2 = 0; i2 < numSortedNodes; i2++) {
            Node node2 = this.mSortedNodes.get(i2);
            if (this.mSetListener == null) {
                this.mSetListener = new AnimatorSetListener(this);
            }
            if (node2.dependencies == null || node2.dependencies.size() == 0) {
                nodesToStart.add(node2);
            } else {
                int numDependencies = node2.dependencies.size();
                for (int j = 0; j < numDependencies; j++) {
                    Dependency dependency = node2.dependencies.get(j);
                    dependency.node.animation.addListener(new DependencyListener(this, node2, dependency.rule));
                }
                node2.tmpDependencies = (ArrayList) node2.dependencies.clone();
            }
            node2.animation.addListener(this.mSetListener);
        }
        if (this.mStartDelay <= 0) {
            Iterator i$4 = nodesToStart.iterator();
            while (i$4.hasNext()) {
                Node node3 = i$4.next();
                node3.animation.start();
                this.mPlayingSet.add(node3.animation);
            }
        } else {
            this.mDelayAnim = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mDelayAnim.setDuration(this.mStartDelay);
            this.mDelayAnim.addListener(new AnimatorListenerAdapter() { // from class: android.animation.AnimatorSet.1
                boolean canceled = false;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator anim) {
                    this.canceled = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator anim) {
                    if (!this.canceled) {
                        int numNodes = nodesToStart.size();
                        for (int i3 = 0; i3 < numNodes; i3++) {
                            Node node4 = (Node) nodesToStart.get(i3);
                            node4.animation.start();
                            AnimatorSet.this.mPlayingSet.add(node4.animation);
                        }
                    }
                    AnimatorSet.this.mDelayAnim = null;
                }
            });
            this.mDelayAnim.start();
        }
        if (this.mListeners != null) {
            ArrayList<Animator.AnimatorListener> tmpListeners = (ArrayList) this.mListeners.clone();
            int numListeners = tmpListeners.size();
            for (int i3 = 0; i3 < numListeners; i3++) {
                tmpListeners.get(i3).onAnimationStart(this);
            }
        }
        if (this.mNodes.size() == 0 && this.mStartDelay == 0) {
            this.mStarted = false;
            if (this.mListeners != null) {
                ArrayList<Animator.AnimatorListener> tmpListeners2 = (ArrayList) this.mListeners.clone();
                int numListeners2 = tmpListeners2.size();
                for (int i4 = 0; i4 < numListeners2; i4++) {
                    tmpListeners2.get(i4).onAnimationEnd(this);
                }
            }
        }
    }

    @Override // android.animation.Animator
    /* renamed from: clone */
    public AnimatorSet mo6clone() {
        AnimatorSet anim = (AnimatorSet) super.mo6clone();
        anim.mNeedsSort = true;
        anim.mTerminated = false;
        anim.mStarted = false;
        anim.mPlayingSet = new ArrayList<>();
        anim.mNodeMap = new HashMap<>();
        anim.mNodes = new ArrayList<>();
        anim.mSortedNodes = new ArrayList<>();
        HashMap<Node, Node> nodeCloneMap = new HashMap<>();
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            Node node = i$.next();
            Node nodeClone = node.m7clone();
            nodeCloneMap.put(node, nodeClone);
            anim.mNodes.add(nodeClone);
            anim.mNodeMap.put(nodeClone.animation, nodeClone);
            nodeClone.dependencies = null;
            nodeClone.tmpDependencies = null;
            nodeClone.nodeDependents = null;
            nodeClone.nodeDependencies = null;
            ArrayList<Animator.AnimatorListener> cloneListeners = nodeClone.animation.getListeners();
            if (cloneListeners != null) {
                ArrayList<Animator.AnimatorListener> listenersToRemove = null;
                Iterator i$2 = cloneListeners.iterator();
                while (i$2.hasNext()) {
                    Animator.AnimatorListener listener = i$2.next();
                    if (listener instanceof AnimatorSetListener) {
                        if (listenersToRemove == null) {
                            listenersToRemove = new ArrayList<>();
                        }
                        listenersToRemove.add(listener);
                    }
                }
                if (listenersToRemove != null) {
                    Iterator i$3 = listenersToRemove.iterator();
                    while (i$3.hasNext()) {
                        cloneListeners.remove(i$3.next());
                    }
                }
            }
        }
        Iterator i$4 = this.mNodes.iterator();
        while (i$4.hasNext()) {
            Node node2 = i$4.next();
            Node nodeClone2 = nodeCloneMap.get(node2);
            if (node2.dependencies != null) {
                Iterator i$5 = node2.dependencies.iterator();
                while (i$5.hasNext()) {
                    Dependency dependency = i$5.next();
                    Node clonedDependencyNode = nodeCloneMap.get(dependency.node);
                    Dependency cloneDependency = new Dependency(clonedDependencyNode, dependency.rule);
                    nodeClone2.addDependency(cloneDependency);
                }
            }
        }
        return anim;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AnimatorSet$DependencyListener.class */
    public static class DependencyListener implements Animator.AnimatorListener {
        private AnimatorSet mAnimatorSet;
        private Node mNode;
        private int mRule;

        public DependencyListener(AnimatorSet animatorSet, Node node, int rule) {
            this.mAnimatorSet = animatorSet;
            this.mNode = node;
            this.mRule = rule;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            if (this.mRule == 1) {
                startIfReady(animation);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
            if (this.mRule == 0) {
                startIfReady(animation);
            }
        }

        private void startIfReady(Animator dependencyAnimation) {
            if (this.mAnimatorSet.mTerminated) {
                return;
            }
            Dependency dependencyToRemove = null;
            int numDependencies = this.mNode.tmpDependencies.size();
            int i = 0;
            while (true) {
                if (i >= numDependencies) {
                    break;
                }
                Dependency dependency = this.mNode.tmpDependencies.get(i);
                if (dependency.rule != this.mRule || dependency.node.animation != dependencyAnimation) {
                    i++;
                } else {
                    dependencyToRemove = dependency;
                    dependencyAnimation.removeListener(this);
                    break;
                }
            }
            this.mNode.tmpDependencies.remove(dependencyToRemove);
            if (this.mNode.tmpDependencies.size() == 0) {
                this.mNode.animation.start();
                this.mAnimatorSet.mPlayingSet.add(this.mNode.animation);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AnimatorSet$AnimatorSetListener.class */
    public class AnimatorSetListener implements Animator.AnimatorListener {
        private AnimatorSet mAnimatorSet;

        AnimatorSetListener(AnimatorSet animatorSet) {
            this.mAnimatorSet = animatorSet;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            if (!AnimatorSet.this.mTerminated && AnimatorSet.this.mPlayingSet.size() == 0 && AnimatorSet.this.mListeners != null) {
                int numListeners = AnimatorSet.this.mListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    AnimatorSet.this.mListeners.get(i).onAnimationCancel(this.mAnimatorSet);
                }
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            animation.removeListener(this);
            AnimatorSet.this.mPlayingSet.remove(animation);
            Node animNode = (Node) this.mAnimatorSet.mNodeMap.get(animation);
            animNode.done = true;
            if (!AnimatorSet.this.mTerminated) {
                ArrayList<Node> sortedNodes = this.mAnimatorSet.mSortedNodes;
                boolean allDone = true;
                int numSortedNodes = sortedNodes.size();
                int i = 0;
                while (true) {
                    if (i < numSortedNodes) {
                        if (sortedNodes.get(i).done) {
                            i++;
                        } else {
                            allDone = false;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (allDone) {
                    if (AnimatorSet.this.mListeners != null) {
                        ArrayList<Animator.AnimatorListener> tmpListeners = (ArrayList) AnimatorSet.this.mListeners.clone();
                        int numListeners = tmpListeners.size();
                        for (int i2 = 0; i2 < numListeners; i2++) {
                            tmpListeners.get(i2).onAnimationEnd(this.mAnimatorSet);
                        }
                    }
                    this.mAnimatorSet.mStarted = false;
                    this.mAnimatorSet.mPaused = false;
                }
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
        }
    }

    private void sortNodes() {
        if (this.mNeedsSort) {
            this.mSortedNodes.clear();
            ArrayList<Node> roots = new ArrayList<>();
            int numNodes = this.mNodes.size();
            for (int i = 0; i < numNodes; i++) {
                Node node = this.mNodes.get(i);
                if (node.dependencies == null || node.dependencies.size() == 0) {
                    roots.add(node);
                }
            }
            ArrayList<Node> tmpRoots = new ArrayList<>();
            while (roots.size() > 0) {
                int numRoots = roots.size();
                for (int i2 = 0; i2 < numRoots; i2++) {
                    Node root = roots.get(i2);
                    this.mSortedNodes.add(root);
                    if (root.nodeDependents != null) {
                        int numDependents = root.nodeDependents.size();
                        for (int j = 0; j < numDependents; j++) {
                            Node node2 = root.nodeDependents.get(j);
                            node2.nodeDependencies.remove(root);
                            if (node2.nodeDependencies.size() == 0) {
                                tmpRoots.add(node2);
                            }
                        }
                    }
                }
                roots.clear();
                roots.addAll(tmpRoots);
                tmpRoots.clear();
            }
            this.mNeedsSort = false;
            if (this.mSortedNodes.size() != this.mNodes.size()) {
                throw new IllegalStateException("Circular dependencies cannot exist in AnimatorSet");
            }
            return;
        }
        int numNodes2 = this.mNodes.size();
        for (int i3 = 0; i3 < numNodes2; i3++) {
            Node node3 = this.mNodes.get(i3);
            if (node3.dependencies != null && node3.dependencies.size() > 0) {
                int numDependencies = node3.dependencies.size();
                for (int j2 = 0; j2 < numDependencies; j2++) {
                    Dependency dependency = node3.dependencies.get(j2);
                    if (node3.nodeDependencies == null) {
                        node3.nodeDependencies = new ArrayList<>();
                    }
                    if (!node3.nodeDependencies.contains(dependency.node)) {
                        node3.nodeDependencies.add(dependency.node);
                    }
                }
            }
            node3.done = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AnimatorSet$Dependency.class */
    public static class Dependency {
        static final int WITH = 0;
        static final int AFTER = 1;
        public Node node;
        public int rule;

        public Dependency(Node node, int rule) {
            this.node = node;
            this.rule = rule;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AnimatorSet$Node.class */
    public static class Node implements Cloneable {
        public Animator animation;
        public ArrayList<Dependency> dependencies = null;
        public ArrayList<Dependency> tmpDependencies = null;
        public ArrayList<Node> nodeDependencies = null;
        public ArrayList<Node> nodeDependents = null;
        public boolean done = false;

        public Node(Animator animation) {
            this.animation = animation;
        }

        public void addDependency(Dependency dependency) {
            if (this.dependencies == null) {
                this.dependencies = new ArrayList<>();
                this.nodeDependencies = new ArrayList<>();
            }
            this.dependencies.add(dependency);
            if (!this.nodeDependencies.contains(dependency.node)) {
                this.nodeDependencies.add(dependency.node);
            }
            Node dependencyNode = dependency.node;
            if (dependencyNode.nodeDependents == null) {
                dependencyNode.nodeDependents = new ArrayList<>();
            }
            dependencyNode.nodeDependents.add(this);
        }

        /* renamed from: clone */
        public Node m7clone() {
            try {
                Node node = (Node) super.clone();
                node.animation = this.animation.mo6clone();
                return node;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    /* loaded from: AnimatorSet$Builder.class */
    public class Builder {
        private Node mCurrentNode;

        Builder(Animator anim) {
            this.mCurrentNode = (Node) AnimatorSet.this.mNodeMap.get(anim);
            if (this.mCurrentNode == null) {
                this.mCurrentNode = new Node(anim);
                AnimatorSet.this.mNodeMap.put(anim, this.mCurrentNode);
                AnimatorSet.this.mNodes.add(this.mCurrentNode);
            }
        }

        public Builder with(Animator anim) {
            Node node = (Node) AnimatorSet.this.mNodeMap.get(anim);
            if (node == null) {
                node = new Node(anim);
                AnimatorSet.this.mNodeMap.put(anim, node);
                AnimatorSet.this.mNodes.add(node);
            }
            Dependency dependency = new Dependency(this.mCurrentNode, 0);
            node.addDependency(dependency);
            return this;
        }

        public Builder before(Animator anim) {
            Node node = (Node) AnimatorSet.this.mNodeMap.get(anim);
            if (node == null) {
                node = new Node(anim);
                AnimatorSet.this.mNodeMap.put(anim, node);
                AnimatorSet.this.mNodes.add(node);
            }
            Dependency dependency = new Dependency(this.mCurrentNode, 1);
            node.addDependency(dependency);
            return this;
        }

        public Builder after(Animator anim) {
            Node node = (Node) AnimatorSet.this.mNodeMap.get(anim);
            if (node == null) {
                node = new Node(anim);
                AnimatorSet.this.mNodeMap.put(anim, node);
                AnimatorSet.this.mNodes.add(node);
            }
            Dependency dependency = new Dependency(node, 1);
            this.mCurrentNode.addDependency(dependency);
            return this;
        }

        public Builder after(long delay) {
            ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
            anim.setDuration(delay);
            after(anim);
            return this;
        }
    }
}